package nl.gjalsem.imagegallery.model.logic.imageproviders;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import nl.gjalsem.imagegallery.model.data.RemoteImage;
import nl.gjalsem.imagegallery.model.logic.CacheFileHandler;
import nl.gjalsem.imagegallery.model.logic.RemoteImagesFetcher;

/**
 * Fetches a list of image URLs from the The Movie Database API.
 */
public class TheMovieDbClient implements RemoteImagesFetcher.RemoteImagesProvider {
    public static class TheMovieDbCacheFileHandler extends CacheFileHandler {
        public TheMovieDbCacheFileHandler(Context context) {
            super(context, "tmdb_config", 1000L * 60L * 60L * 24L * 2L);
        }
    }

    private static class TheMovieDbConfig {
        private final String baseImageUrl;
        private final String thumbnailSize;
        private final String fullImageSize;

        private TheMovieDbConfig(String baseImageUrl, String thumbnailSize, String fullImageSize) {
            this.baseImageUrl = baseImageUrl;
            this.thumbnailSize = thumbnailSize;
            this.fullImageSize = fullImageSize;
        }

        private static TheMovieDbConfig from(JSONObject configJson) throws JSONException {
            JSONArray backdropSizes = configJson.getJSONArray("backdrop_sizes");
            String baseImageUrl = configJson.getString("secure_base_url");
            String thumbnailSize = backdropSizes.getString(0);
            String fullImageSize = backdropSizes.getString(backdropSizes.length() - 1);
            return new TheMovieDbConfig(baseImageUrl, thumbnailSize, fullImageSize);
        }
    }

    private static final String TAG = TheMovieDbClient.class.getSimpleName();
    private static final Object REQUEST_TAG = TheMovieDbClient.class;
    private static final String API_KEY = "";
    private static final String END_POINT = "https://api.themoviedb.org/3/";
    private static final String CONFIGURATION_PATH = "configuration?api_key=" + API_KEY;
    private static final String POPULAR_MOVIES_PATH = "movie/popular?api_key=" + API_KEY + "&page=%d";
    private static final String ERROR_MESSAGE = "Error loading images from The Movie Database";

    private final RequestQueue requestQueue;
    private final CacheFileHandler cachedConfig;

    private TheMovieDbConfig config;

    public TheMovieDbClient(RequestQueue requestQueue, CacheFileHandler cachedConfig) {
        this.requestQueue = requestQueue;
        this.cachedConfig = cachedConfig;
    }

    @Override
    public void fetchImages(int page, RemoteImagesFetcher.ProviderCallback callback) {
        if (TextUtils.isEmpty(API_KEY)) {
            callback.onError("Please set TheMovieDbClient.API_KEY to try out this app.");
        } else if (config == null) {
            cachedConfig.load(cachedConfig -> onCachedConfigLoaded(cachedConfig, page, callback));
        } else {
            requestImages(page, callback);
        }
    }

    @Override
    public void cancel() {
        cachedConfig.cancel();
        requestQueue.cancelAll(REQUEST_TAG);
    }

    private void onCachedConfigLoaded(byte[] data, int page, RemoteImagesFetcher.ProviderCallback callback) {
        if (data == null) {
            requestConfig(page, callback);
            return;
        }

        String cachedConfig = new String(data, StandardCharsets.UTF_8);
        Log.d(TAG, "Using cached config: " + cachedConfig);
        try {
            JSONObject configJson = new JSONObject(cachedConfig);
            config = TheMovieDbConfig.from(configJson);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing cache: " + cachedConfig, e);
            requestConfig(page, callback);
            return;
        }

        requestImages(page, callback);
    }

    private void requestConfig(int page, RemoteImagesFetcher.ProviderCallback callback) {
        Request<?> request = new JsonObjectRequest(END_POINT + CONFIGURATION_PATH, null,
                response -> onConfigResponse(response, page, callback),
                error -> onVolleyError(error, callback)).setTag(REQUEST_TAG);

        // We already cache the result in a separate file that won't be affected by other requests.
        request.setShouldCache(false);

        requestQueue.add(request);
    }

    private void onConfigResponse(JSONObject response, int page, RemoteImagesFetcher.ProviderCallback callback) {
        String configString;
        try {
            JSONObject configJson = response.getJSONObject("images");
            config = TheMovieDbConfig.from(configJson);
            configString = configJson.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing response: " + response, e);
            callback.onError(ERROR_MESSAGE);
            return;
        }
        Log.d(TAG, "Fetched new config: " + configString);
        cachedConfig.save(configString.getBytes(StandardCharsets.UTF_8),
                () -> requestImages(page, callback));
    }

    private void requestImages(int page, RemoteImagesFetcher.ProviderCallback callback) {
        String url = String.format(Locale.US, END_POINT + POPULAR_MOVIES_PATH, page);
        requestQueue.add(new JsonObjectRequest(url, null,
                response -> onImagesResponse(response, page, callback),
                error -> onVolleyError(error, callback)).setTag(REQUEST_TAG));
    }

    private void onImagesResponse(JSONObject response, int page, RemoteImagesFetcher.ProviderCallback callback) {
        int totalPages;
        List<RemoteImage> images;
        try {
            totalPages = response.getInt("total_pages");
            images = parseImagesJson(response);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing response: " + response, e);
            callback.onError(ERROR_MESSAGE);
            return;
        }
        callback.onSuccess(images, images.isEmpty() || page >= totalPages);
    }

    private List<RemoteImage> parseImagesJson(JSONObject response) throws JSONException {
        JSONArray results = response.getJSONArray("results");
        List<RemoteImage> images = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject movie = results.getJSONObject(i);
            String backdropPath = movie.optString("backdrop_path");

            // Some movies have the string "null" as backdrop path value.
            if (backdropPath.startsWith("/")) {
                String thumbnailUrl = config.baseImageUrl + config.thumbnailSize + backdropPath;
                String fullImageUrl = config.baseImageUrl + config.fullImageSize + backdropPath;
                images.add(new RemoteImage(thumbnailUrl, fullImageUrl));
            }
        }
        return Collections.unmodifiableList(images);
    }

    private static void onVolleyError(VolleyError error, RemoteImagesFetcher.ProviderCallback callback) {
        Log.e(TAG, ERROR_MESSAGE, error);
        callback.onError(ERROR_MESSAGE);
    }
}

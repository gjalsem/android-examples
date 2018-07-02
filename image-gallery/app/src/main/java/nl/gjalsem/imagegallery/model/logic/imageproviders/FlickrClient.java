package nl.gjalsem.imagegallery.model.logic.imageproviders;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import nl.gjalsem.imagegallery.model.data.RemoteImage;
import nl.gjalsem.imagegallery.model.logic.RemoteImagesFetcher;

/**
 * Fetches a list of image URLs from the Flickr API.
 *
 * For the full size URL we use the "large" variant of the image. Fetching the image in its original
 * size requires authentication, which currently isn't implemented.
 */
public class FlickrClient implements RemoteImagesFetcher.RemoteImagesProvider {
    private static final String TAG = FlickrClient.class.getSimpleName();
    private static final Object REQUEST_TAG = FlickrClient.class;
    private static final String API_KEY = "";
    private static final String METHOD = "flickr.photos.getRecent";
    private static final int PER_PAGE = 50;
    private static final String END_POINT = "https://api.flickr.com/services/rest/?format=json&api_key=" + API_KEY + "&method=" + METHOD + "&per_page=" + PER_PAGE + "&page=%d";
    private static final String THUMBNAIL_URL = "https://farm%s.staticflickr.com/%s/%s_%s_n.jpg";
    private static final String FULL_SIZE_URL = "https://farm%s.staticflickr.com/%s/%s_%s_b.jpg";
    private static final String RESPONSE_PREFIX = "jsonFlickrApi(";
    private static final String RESPONSE_SUFFIX = ")";
    private static final String ERROR_MESSAGE = "Error loading images from Flickr";

    private final RequestQueue requestQueue;

    public FlickrClient(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    @Override
    public void fetchImages(int page, RemoteImagesFetcher.RemoteImagesCallback callback) {
        if (TextUtils.isEmpty(API_KEY)) {
            callback.onError("Please set FlickrClient.API_KEY to try out this app.");
            return;
        }

        String url = String.format(Locale.US, END_POINT, page);

        // Flickr's response needs some preprocessing before it is valid JSON, so we can't use a
        // JsonObjectRequest.

        requestQueue.add(new StringRequest(url, response -> {
            List<RemoteImage> images = parseResponse(response);
            if (images == null) {
                callback.onError(ERROR_MESSAGE);
            } else {
                callback.onSuccess(images, images.size() < PER_PAGE);
            }
        }, error -> {
            Log.e(TAG, ERROR_MESSAGE, error);
            callback.onError(ERROR_MESSAGE);
        }).setTag(REQUEST_TAG));
    }

    @Override
    public void cancel() {
        requestQueue.cancelAll(REQUEST_TAG);
    }

    private static List<RemoteImage> parseResponse(String response) {
        if (!response.startsWith(RESPONSE_PREFIX) || !response.endsWith(RESPONSE_SUFFIX)) {
            Log.e(TAG, "Unexpected response format: " + response);
            return null;
        }

        response = response.substring(RESPONSE_PREFIX.length(),
                response.length() - RESPONSE_SUFFIX.length());

        try {
            JSONObject root = new JSONObject(response);
            JSONArray photos = root.getJSONObject("photos").getJSONArray("photo");
            List<RemoteImage> images = new ArrayList<>(photos.length());

            for (int i = 0; i < photos.length(); i++) {
                JSONObject photo = photos.getJSONObject(i);
                String farm = photo.getString("farm");
                String server = photo.getString("server");
                String id = photo.getString("id");
                String secret = photo.getString("secret");

                String thumbnailUrl = String.format(Locale.US, THUMBNAIL_URL, farm, server, id,
                        secret);
                String fullSizeUrl = String.format(Locale.US, FULL_SIZE_URL, farm, server, id,
                        secret);

                images.add(new RemoteImage(thumbnailUrl, fullSizeUrl));
            }

            return Collections.unmodifiableList(images);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing response", e);
            return null;
        }
    }
}

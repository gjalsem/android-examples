package nl.gjalsem.imagegallery.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import nl.gjalsem.imagegallery.model.logic.BitmapCache;
import nl.gjalsem.imagegallery.model.logic.RemoteImagesFetcher;
import nl.gjalsem.imagegallery.model.logic.imageproviders.TheMovieDbClient;

/**
 * Factory class for MainViewModel.
 *
 * TODO: Currently this class handles object creation for dependency injection. It may be worth
 * considering using a dependency injection library instead.
 *
 * TODO: The RemoteImagesProvider implementation is hardcoded here, it would be nice to have the
 * option to choose one in the UI.
 */
public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    private MainViewModelFactory(Context context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // TODO: This calls Context.getCacheDir(), which is a disk operation, so strictly speaking
        // it should not be done on the main thread. The performance impact should be minimal
        // though, so for now it's fine. We could however call getCacheDir() ourselves and create
        // RequestQueue manually, so that we can use the cache dir in TheMovieDbClient too, without
        // passing it a context.
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        RemoteImagesFetcher remoteImagesFetcher = new RemoteImagesFetcher(
                new TheMovieDbClient(requestQueue,
                        new TheMovieDbClient.TheMovieDbCacheFileHandler(context)));

        ImageLoader imageLoader = new ImageLoader(requestQueue, new BitmapCache());

        return (T) new MainViewModel(remoteImagesFetcher, imageLoader);
    }

    /**
     * Helper method that wraps ViewModelProviders to get a MainViewModel.
     */
    public static MainViewModel get(FragmentActivity activity) {
        return ViewModelProviders.of(activity, new MainViewModelFactory(activity)).get(
                MainViewModel.class);
    }
}

package nl.gjalsem.imagegallery.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import nl.gjalsem.imagegallery.model.data.RemoteImage;
import nl.gjalsem.imagegallery.model.logic.RemoteImagesFetcher;
import nl.gjalsem.imagegallery.viewmodel.lifecycle.NonNullLiveData;
import nl.gjalsem.imagegallery.viewmodel.lifecycle.NonNullMutableLiveData;

/**
 * The main view model which handles the view's data, such as the list of images to show.
 */
public class MainViewModel extends ViewModel {
    public static final int POSITION_NONE = -1;

    private final RemoteImagesFetcher remoteImagesFetcher;
    private final ImageLoader imageLoader;
    private final NonNullMutableLiveData<MainViewState> state = new NonNullMutableLiveData<>(
            new MainViewState.Builder().setSelectedImagePosition(POSITION_NONE).build());

    MainViewModel(RemoteImagesFetcher remoteImagesFetcher, ImageLoader imageLoader) {
        this.remoteImagesFetcher = remoteImagesFetcher;
        this.imageLoader = imageLoader;
    }

    @Override
    protected void onCleared() {
        remoteImagesFetcher.cancel();
    }

    public void init(MainViewState loadedState) {
        if (loadedState != null) {
            state.setValue(loadedState);
        }

        if (state.getValue().getRemoteImages().isEmpty()) {
            fetchMoreImages();
        }
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public NonNullLiveData<MainViewState> getState() {
        return state;
    }

    public void onThumbnailShown(int position) {
        checkMoreImages(position);
    }

    public void onFullImageShown(int position) {
        state.setValue(state.getValue().toBuilder().setSelectedImagePosition(position).build());
        checkMoreImages(position);
    }

    public void onSelectedImageShown() {
        state.setValue(
                state.getValue().toBuilder().setSelectedImagePosition(POSITION_NONE).build());
    }

    public void onErrorDismissed() {
        state.setValue(state.getValue().toBuilder().setError(null).build());
    }

    private void checkMoreImages(int position) {
        if (position >= state.getValue().getRemoteImages().size() - 1) {
            fetchMoreImages();
        }
    }

    private void fetchMoreImages() {
        state.setValue(state.getValue().toBuilder().setLoading(true).build());

        remoteImagesFetcher.updateImages(state.getValue().getRemoteImages(),
                new RemoteImagesFetcher.RemoteImagesCallback() {
                    @Override
                    public void onSuccess(List<RemoteImage> images, boolean endOfList) {
                        state.setValue(state.getValue().toBuilder().setRemoteImages(
                                images).setLoading(!endOfList).build());
                    }

                    @Override
                    public void onError(String msg) {
                        state.setValue(state.getValue().toBuilder().setLoading(false).setError(
                                msg).build());
                    }
                });
    }
}

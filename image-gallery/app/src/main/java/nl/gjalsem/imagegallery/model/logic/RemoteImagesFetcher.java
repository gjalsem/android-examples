package nl.gjalsem.imagegallery.model.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.gjalsem.imagegallery.model.data.RemoteImage;

/**
 * Fetches lists of remote image URLs by calling a RemoteImagesProvider implementation.
 */
public class RemoteImagesFetcher {
    public static final int NO_NEXT_PAGE = -1;

    public interface RemoteImagesCallback {
        void onSuccess(List<RemoteImage> images, int nextPage);

        void onError(String msg);
    }

    public interface ProviderCallback {
        void onSuccess(List<RemoteImage> images, boolean endOfList);

        void onError(String msg);
    }

    public interface RemoteImagesProvider {
        void fetchImages(int page, ProviderCallback callback);

        void cancel();
    }

    private final RemoteImagesProvider remoteImagesProvider;

    private boolean fetching;

    public RemoteImagesFetcher(RemoteImagesProvider remoteImagesProvider) {
        this.remoteImagesProvider = remoteImagesProvider;
    }

    public void updateImages(int page, List<RemoteImage> originalImages, RemoteImagesCallback callback) {
        if (fetching) {
            return;
        }

        fetching = true;
        remoteImagesProvider.fetchImages(page, new ProviderCallback() {
            @Override
            public void onSuccess(List<RemoteImage> images, boolean endOfList) {
                fetching = false;

                int nextPage = endOfList ? NO_NEXT_PAGE : page + 1;

                if (originalImages == null) {
                    callback.onSuccess(images, nextPage);
                } else {
                    List<RemoteImage> updatedImages = append(originalImages, images);
                    if (!endOfList && updatedImages.size() == originalImages.size()) {
                        updateImages(nextPage, originalImages, callback);
                    } else {
                        callback.onSuccess(updatedImages, nextPage);
                    }
                }
            }

            @Override
            public void onError(String msg) {
                fetching = false;
                callback.onError(msg);
            }
        });
    }

    public void cancel() {
        remoteImagesProvider.cancel();
    }

    /**
     * Appends items from list2 to list1, skipping any duplicates.
     *
     * With paginated data you sometimes get duplicates. Not much we can do but filter them out.
     */
    private static <T> List<T> append(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>(list1);
        for (T item : list2) {
            if (!result.contains(item)) {
                result.add(item);
            }
        }
        return Collections.unmodifiableList(result);
    }
}

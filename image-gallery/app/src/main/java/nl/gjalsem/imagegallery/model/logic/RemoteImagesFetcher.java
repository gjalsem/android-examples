package nl.gjalsem.imagegallery.model.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.gjalsem.imagegallery.model.data.RemoteImage;

/**
 * Fetches lists of remote image URLs by calling a RemoteImagesProvider implementation. Keeps track
 * of pagination and when to stop fetching.
 */
public class RemoteImagesFetcher {
    public interface RemoteImagesCallback {
        void onSuccess(List<RemoteImage> images, boolean endOfList);

        void onError(String msg);
    }

    public interface RemoteImagesProvider {
        void fetchImages(int page, RemoteImagesCallback callback);

        void cancel();
    }

    private final RemoteImagesProvider remoteImagesProvider;

    private int page = 1;
    private boolean stopFetching;
    private boolean fetching;

    public RemoteImagesFetcher(RemoteImagesProvider remoteImagesProvider) {
        this.remoteImagesProvider = remoteImagesProvider;
    }

    public void updateImages(List<RemoteImage> originalImages, RemoteImagesCallback callback) {
        if (fetching || stopFetching) {
            return;
        }

        fetching = true;
        remoteImagesProvider.fetchImages(page, new RemoteImagesCallback() {
            @Override
            public void onSuccess(List<RemoteImage> images, boolean endOfList) {
                fetching = false;
                page++;

                if (endOfList) {
                    stopFetching = true;
                }

                if (originalImages == null) {
                    callback.onSuccess(images, endOfList);
                } else {
                    List<RemoteImage> updatedImages = append(originalImages, images);
                    if (updatedImages.size() == originalImages.size()) {
                        updateImages(originalImages, callback);
                    } else {
                        callback.onSuccess(updatedImages, endOfList);
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

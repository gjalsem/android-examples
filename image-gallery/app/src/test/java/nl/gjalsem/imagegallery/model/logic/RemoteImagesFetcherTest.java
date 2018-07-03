package nl.gjalsem.imagegallery.model.logic;

import org.junit.Test;

import java.util.List;

import nl.gjalsem.imagegallery.model.data.RemoteImage;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RemoteImagesFetcherTest {
    private static class MockImagesProvider implements RemoteImagesFetcher.RemoteImagesProvider {
        private int fetchCalled;
        private int cancelCalled;
        private RemoteImagesFetcher.ProviderCallback callback;

        @Override
        public void fetchImages(int page, RemoteImagesFetcher.ProviderCallback callback) {
            fetchCalled++;
            this.callback = callback;
        }

        @Override
        public void cancel() {
            cancelCalled++;
        }

        void mockDoneFetching() {
            callback.onSuccess(null, false);
        }
    }

    private static class MockImagesCallback implements RemoteImagesFetcher.RemoteImagesCallback {
        @Override
        public void onSuccess(List<RemoteImage> images, int nextPage) {
        }

        @Override
        public void onError(String msg) {
        }
    }

    @Test
    public void fetchImages() {
        MockImagesProvider mockImagesProvider = new MockImagesProvider();
        RemoteImagesFetcher fetcher = new RemoteImagesFetcher(mockImagesProvider);
        fetcher.updateImages(1, null, new MockImagesCallback());
        fetcher.updateImages(1, null, new MockImagesCallback());
        assertThat(mockImagesProvider.fetchCalled, is(1));
        assertThat(mockImagesProvider.cancelCalled, is(0));
        mockImagesProvider.mockDoneFetching();
        fetcher.updateImages(1, null, new MockImagesCallback());
        assertThat(mockImagesProvider.fetchCalled, is(2));
        assertThat(mockImagesProvider.cancelCalled, is(0));
        fetcher.cancel();
        assertThat(mockImagesProvider.fetchCalled, is(2));
        assertThat(mockImagesProvider.cancelCalled, is(1));
    }
}

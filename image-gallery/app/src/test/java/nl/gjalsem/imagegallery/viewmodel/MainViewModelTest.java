package nl.gjalsem.imagegallery.viewmodel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import nl.gjalsem.imagegallery.model.data.RemoteImage;
import nl.gjalsem.imagegallery.model.logic.RemoteImagesFetcher;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;

/**
 * TODO: Add more tests.
 */
@SuppressWarnings("ConstantConditions")
@RunWith(MockitoJUnitRunner.class)
public class MainViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RemoteImagesFetcher remoteImagesFetcher;

    @Before
    public void setup() {
        doAnswer(invocation -> {
            int page = invocation.getArgument(0);
            List<RemoteImage> images = invocation.getArgument(1);
            RemoteImagesFetcher.RemoteImagesCallback callback = invocation.getArgument(2);

            List<RemoteImage> result = images == null ? new ArrayList<>() : new ArrayList<>(images);
            for (int i = 0; i < 20; i++) {
                result.add(new RemoteImage(null, null));
            }

            callback.onSuccess(result, page + 1);
            return null;
        }).when(remoteImagesFetcher).updateImages(anyInt(), any(), any());
    }

    @Test
    public void fetchImages() {
        MainViewModel viewModel = new MainViewModel(remoteImagesFetcher, null);
        viewModel.init(null);
        assertThat(viewModel.getState().getValue().getRemoteImages().size(), is(20));

        viewModel.onThumbnailShown(19);
        assertThat(viewModel.getState().getValue().getRemoteImages().size(), is(40));

        viewModel.onFullImageShown(39);
        assertThat(viewModel.getState().getValue().getRemoteImages().size(), is(60));
    }

    @Test
    public void setSelectedImagePosition() {
        MainViewModel viewModel = new MainViewModel(remoteImagesFetcher, null);
        viewModel.onFullImageShown(11);
        assertThat(viewModel.getState().getValue().getSelectedImagePosition(), is(11));

        viewModel.onSelectedImageShown();
        assertThat(viewModel.getState().getValue().getSelectedImagePosition(),
                is(MainViewModel.POSITION_NONE));
    }

    @Test
    public void saveAndLoadState() {
        MainViewModel viewModel = new MainViewModel(remoteImagesFetcher, null);
        viewModel.init(null);
        assertThat(viewModel.getState().getValue().getRemoteImages().size(), is(20));

        MainViewState state = viewModel.getState().getValue();
        MainViewModel viewModel2 = new MainViewModel(remoteImagesFetcher, null);
        viewModel2.init(state);
        assertThat(viewModel2.getState().getValue(), equalTo(state));
    }
}

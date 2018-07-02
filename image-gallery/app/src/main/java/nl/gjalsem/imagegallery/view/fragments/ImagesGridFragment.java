package nl.gjalsem.imagegallery.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.gjalsem.imagegallery.R;
import nl.gjalsem.imagegallery.model.data.RemoteImage;
import nl.gjalsem.imagegallery.view.adapters.RemoteImageGridLayoutManager;
import nl.gjalsem.imagegallery.view.adapters.RemoteImageRecyclerViewAdapter;
import nl.gjalsem.imagegallery.viewmodel.MainViewModel;
import nl.gjalsem.imagegallery.viewmodel.MainViewModelFactory;
import nl.gjalsem.imagegallery.viewmodel.MainViewState;

/**
 * Shows the list of images in a endlessly scrollable grid.
 */
public class ImagesGridFragment extends Fragment {
    private MainViewModel viewModel;
    private RemoteImageRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = MainViewModelFactory.get(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_images_grid, container, false);

        adapter = new RemoteImageRecyclerViewAdapter(viewModel.getImageLoader(),
                R.layout.grid_image, R.layout.grid_footer, RemoteImage::getThumbnailUrl,
                this::onThumbnailClicked);
        adapter.setMaxImageSize(getResources().getDimensionPixelSize(R.dimen.grid_image_min_width),
                getResources().getDimensionPixelSize(R.dimen.grid_image_height));

        RemoteImageGridLayoutManager layoutManager = new RemoteImageGridLayoutManager(getContext(),
                adapter);

        recyclerView = view.findViewById(R.id.images_grid);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    viewModel.onThumbnailShown(layoutManager.findLastVisibleItemPosition());
                }
            }
        });

        viewModel.getState().observe(this, this::onStateChanged);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentUtil.setFullScreen(this, false);
    }

    @Override
    public void onDestroyView() {
        viewModel.getState().removeObservers(this);
        recyclerView = null;
        adapter = null;

        super.onDestroyView();
    }

    private void onStateChanged(MainViewState state) {
        adapter.update(state.getRemoteImages(), state.isLoading());

        if (state.getSelectedImagePosition() != MainViewModel.POSITION_NONE) {
            // RecyclerView needs a layout pass before it knows which direction to scroll.
            recyclerView.post(
                    () -> recyclerView.scrollToPosition(state.getSelectedImagePosition()));
            viewModel.onSelectedImageShown();
        }
    }

    private void onThumbnailClicked(int position) {
        // TODO: Try a shared element transition? Useful links:
        // https://android-developers.googleblog.com/2018/02/continuous-shared-element-transitions.html
        // https://medium.com/google-developers/fragment-transitions-ea2726c3f36f
        requireFragmentManager().beginTransaction().setTransition(
                FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,
                FullImageFragment.newInstance(position)).addToBackStack(null).commit();
    }
}

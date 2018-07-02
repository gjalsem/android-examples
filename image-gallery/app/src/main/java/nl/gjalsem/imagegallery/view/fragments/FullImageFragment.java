package nl.gjalsem.imagegallery.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.gjalsem.imagegallery.R;
import nl.gjalsem.imagegallery.model.data.RemoteImage;
import nl.gjalsem.imagegallery.view.adapters.OnSizeChangedListener;
import nl.gjalsem.imagegallery.view.adapters.RemoteImageRecyclerViewAdapter;
import nl.gjalsem.imagegallery.viewmodel.MainViewModel;
import nl.gjalsem.imagegallery.viewmodel.MainViewModelFactory;

/**
 * Shows a full size image and allows horizontal swiping between all images.
 */
public class FullImageFragment extends Fragment {
    private static final String ARG_IMAGE_POSITION = "image_position";

    private MainViewModel viewModel;
    private RecyclerView recyclerView;

    public static FullImageFragment newInstance(int imagePosition) {
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_POSITION, imagePosition);

        FullImageFragment fragment = new FullImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = MainViewModelFactory.get(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_image, container, false);

        RemoteImageRecyclerViewAdapter adapter = new RemoteImageRecyclerViewAdapter(
                viewModel.getImageLoader(), R.layout.full_image, R.layout.full_loading,
                RemoteImage::getFullSizeUrl, null);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL, false);

        // Full size images can take up quite some memory. Only load them when actually needed.
        layoutManager.setItemPrefetchEnabled(false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnLayoutChangeListener(new OnSizeChangedListener() {
            @Override
            protected void onSizeChanged(int width, int height) {
                adapter.setMaxImageSize(width, height);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    viewModel.onFullImageShown(layoutManager.findFirstVisibleItemPosition());
                }
            }
        });

        new PagerSnapHelper().attachToRecyclerView(recyclerView);

        Bundle args = getArguments();
        if (args != null) {
            recyclerView.scrollToPosition(args.getInt(ARG_IMAGE_POSITION));
        }

        viewModel.getState().observe(this,
                state -> adapter.update(state.getRemoteImages(), state.isLoading()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentUtil.setFullScreen(this, true);
    }

    @Override
    public void onDestroyView() {
        viewModel.getState().removeObservers(this);
        recyclerView = null;

        super.onDestroyView();
    }
}

package nl.gjalsem.imagegallery.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import nl.gjalsem.imagegallery.R;
import nl.gjalsem.imagegallery.view.fragments.ImagesGridFragment;
import nl.gjalsem.imagegallery.viewmodel.MainViewModel;
import nl.gjalsem.imagegallery.viewmodel.MainViewModelFactory;
import nl.gjalsem.imagegallery.viewmodel.MainViewState;

/**
 * The app's main activity. Handles fragments to show its different screens and shows errors as
 * toast.
 */
public class MainActivity extends AppCompatActivity {
    private static final String STATE_KEY = MainActivity.class.getName() + ".State";

    private MainViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ImagesGridFragment()).commit();
        }

        viewModel = MainViewModelFactory.get(this);
        viewModel.init(
                savedInstanceState == null ? null : savedInstanceState.getParcelable(STATE_KEY));
        viewModel.getState().observe(this, this::onStateChanged);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_KEY, viewModel.getState().getValue());
    }

    private void onStateChanged(MainViewState state) {
        if (state.getError() != null) {
            Toast.makeText(this, state.getError(), Toast.LENGTH_LONG).show();
            viewModel.onErrorDismissed();
        }
    }
}

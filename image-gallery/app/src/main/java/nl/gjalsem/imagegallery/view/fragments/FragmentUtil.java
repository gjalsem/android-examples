package nl.gjalsem.imagegallery.view.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Fragment related helper methods.
 */
public final class FragmentUtil {
    private FragmentUtil() {
    }

    public static void setFullScreen(Fragment fragment, boolean fullScreen) {
        View view = fragment.getView();
        ActionBar actionBar = ((AppCompatActivity) fragment.requireActivity()).getSupportActionBar();
        if (view == null || actionBar == null) {
            throw new IllegalStateException("Fragment should have a view and action bar");
        }

        if (fullScreen) {
            view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN);
            actionBar.hide();
        } else {
            view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            actionBar.show();
        }
    }
}

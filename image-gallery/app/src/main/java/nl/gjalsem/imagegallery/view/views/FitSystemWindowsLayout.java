package nl.gjalsem.imagegallery.view.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * A FrameLayout that remembers the fitSystemWindows insets and reapplies them each time a child
 * view is added.
 *
 * Convenient for example as a fragment container, so that the fitsSystemWindows flag works on the
 * fragment views.
 */
public class FitSystemWindowsLayout extends FrameLayout {
    private final Rect insets = new Rect();

    public FitSystemWindowsLayout(@NonNull Context context) {
        super(context);
    }

    public FitSystemWindowsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FitSystemWindowsLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean fitSystemWindows(Rect insets) {
        this.insets.set(insets);
        return super.fitSystemWindows(insets);
    }

    @TargetApi(23)
    @SuppressWarnings("deprecation")
    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        super.fitSystemWindows(insets);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (Build.VERSION.SDK_INT < 23) {
            super.fitSystemWindows(insets);
        }
    }
}

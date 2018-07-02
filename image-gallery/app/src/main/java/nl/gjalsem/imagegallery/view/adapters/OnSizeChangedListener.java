package nl.gjalsem.imagegallery.view.adapters;

import android.view.View;

/**
 * An OnLayoutChangeListener that calls onSizeChanged() when the view's size has changed.
 *
 * Unlike onLayoutChange(), onSizeChanged() is called after the layout pass is done.
 */
public abstract class OnSizeChangedListener implements View.OnLayoutChangeListener {
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        int width = right - left;
        int height = bottom - top;
        int oldWidth = oldRight - oldLeft;
        int oldHeight = oldBottom - oldTop;
        if (width != oldWidth || height != oldHeight) {
            v.post(() -> onSizeChanged(width, height));
        }
    }

    protected abstract void onSizeChanged(int width, int height);
}

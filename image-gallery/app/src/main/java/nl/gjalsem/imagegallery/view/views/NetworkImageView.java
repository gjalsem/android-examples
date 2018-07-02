package nl.gjalsem.imagegallery.view.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * An ImageView wrapper which can load and show remote images.
 */
public class NetworkImageView extends FrameLayout {
    private static final String TAG = NetworkImageView.class.getSimpleName();

    private final ImageView imageView;

    private View placeholderView;
    private View errorView;
    private ImageLoader.ImageContainer imageContainer;

    public NetworkImageView(Context context) {
        this(context, null);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        imageView = new AppCompatImageView(context);
        initScaleType(attrs);
        addView(imageView);
    }

    public void setPlaceholderView(View placeholderView) {
        if (this.placeholderView != null) {
            removeView(this.placeholderView);
        }
        this.placeholderView = placeholderView;
        addView(placeholderView, 0);
    }

    public void setErrorView(View errorView) {
        if (this.errorView != null) {
            removeView(this.errorView);
        }
        this.errorView = errorView;
        addView(errorView, 0);
    }

    public void clear() {
        loadImage(null, null, 0, 0);
    }

    /**
     * Unlike Volley's NetworkImageView, we don't let this view determine the max image size itself.
     * The view can only determine the max image size based on its own size after a layout pass,
     * meaning that cached images will always appear with a one frame delay, which can cause a
     * subtle but unwanted flickering when scrolling through images.
     */
    public void loadImage(String url, ImageLoader imageLoader, int maxWidth, int maxHeight) {
        if (url == null) {
            if (imageContainer != null) {
                imageContainer.cancelRequest();
                imageContainer = null;
            }
            updateViews(null, false, false);
            return;
        }

        if (imageContainer != null) {
            if (url.equals(imageContainer.getRequestUrl())) {
                return;
            }
            imageContainer.cancelRequest();
        }

        imageContainer = imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                updateViews(response.getBitmap(), !isImmediate, false);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error loading image", error);
                updateViews(null, false, true);
            }
        }, maxWidth, maxHeight, imageView.getScaleType());
    }

    private void updateViews(Bitmap bitmap, boolean fadeIn, boolean error) {
        if (placeholderView != null) {
            placeholderView.setVisibility(error ? View.GONE : View.VISIBLE);
        }

        if (errorView != null) {
            errorView.setVisibility(error ? View.VISIBLE : View.GONE);
        }

        imageView.clearAnimation();
        imageView.setAlpha(fadeIn ? 0f : 1f);
        imageView.setVisibility(bitmap == null ? View.GONE : View.VISIBLE);
        imageView.setImageBitmap(bitmap);
        if (fadeIn && bitmap != null) {
            // Because we use a separate placeholder view, we can easily have a smooth transition
            // from placeholder to image, without the pitfalls of fading between drawables in a
            // single ImageView.
            imageView.animate().alpha(1f).setDuration(200).withEndAction(
                    this::hidePlaceholderView).start();
        }
    }

    private void hidePlaceholderView() {
        if (placeholderView != null) {
            placeholderView.setVisibility(View.GONE);
        }
    }

    private void initScaleType(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                new int[]{android.R.attr.scaleType});
        int index = a.getInt(0, -1);
        a.recycle();
        if (index >= 0) {
            imageView.setScaleType(ImageView.ScaleType.values()[index]);
        }
    }
}

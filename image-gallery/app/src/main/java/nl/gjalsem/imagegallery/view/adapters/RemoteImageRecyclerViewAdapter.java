package nl.gjalsem.imagegallery.view.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

import java.util.Collections;
import java.util.List;

import nl.gjalsem.imagegallery.R;
import nl.gjalsem.imagegallery.model.data.RemoteImage;
import nl.gjalsem.imagegallery.view.views.NetworkImageView;

/**
 * A RecyclerView Adapter which shows RemoteImages, with a loading view at the end.
 */
public class RemoteImageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface UrlProvider {
        String getUrl(RemoteImage remoteImage);
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {
        private final NetworkImageView imageView;

        private ImageViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(imageViewId, parent, false));

            Context context = parent.getContext();
            imageView = itemView.findViewById(R.id.image_view);
            imageView.setPlaceholderView(createImageView(context, R.drawable.ic_image));
            imageView.setErrorView(createImageView(context, R.drawable.ic_broken_image));
            imageView.clear();

            if (itemClickedListener != null) {
                itemView.setOnClickListener(
                        view -> itemClickedListener.onItemClicked(getAdapterPosition()));
            }
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        private FooterViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(loadingViewId, parent, false));
        }
    }

    private static final int VIEW_TYPE_IMAGE = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private final ImageLoader imageLoader;
    private final int imageViewId;
    private final int loadingViewId;
    private final UrlProvider urlProvider;
    private final OnItemClickListener itemClickedListener;

    private List<RemoteImage> remoteImages = Collections.emptyList();
    private boolean loading;
    private int maxImageWidth;
    private int maxImageHeight;

    public RemoteImageRecyclerViewAdapter(ImageLoader imageLoader, int imageViewId, int loadingViewId, @NonNull UrlProvider urlProvider, @Nullable OnItemClickListener itemClickedListener) {
        this.imageLoader = imageLoader;
        this.imageViewId = imageViewId;
        this.loadingViewId = loadingViewId;
        this.urlProvider = urlProvider;
        this.itemClickedListener = itemClickedListener;
    }

    /**
     * Must be called with values > 0 before images are shown.
     */
    public void setMaxImageSize(int width, int height) {
        maxImageWidth = width;
        maxImageHeight = height;
        notifyItemRangeChanged(0, getItemCount());
    }

    public void update(@NonNull List<RemoteImage> remoteImages, boolean loading) {
        int oldItemCount = getItemCount();
        boolean wasLoading = this.loading;

        this.remoteImages = remoteImages;
        this.loading = loading;

        int newItemCount = getItemCount();
        if (newItemCount == oldItemCount) {
            return;
        }

        if (wasLoading) {
            int updatePosition = oldItemCount - 1;
            notifyItemRemoved(updatePosition);
            notifyItemRangeInserted(updatePosition, newItemCount - updatePosition);
        } else {
            notifyItemRangeInserted(oldItemCount, newItemCount - oldItemCount);
        }
    }

    boolean isFooter(int position) {
        return getItemViewType(position) == VIEW_TYPE_FOOTER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return viewType == VIEW_TYPE_FOOTER ? new FooterViewHolder(inflater,
                parent) : new ImageViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (maxImageWidth > 0 && maxImageHeight > 0 && holder instanceof ImageViewHolder) {
            NetworkImageView imageView = ((ImageViewHolder) holder).imageView;
            imageView.loadImage(
                    remoteImages.isEmpty() ? null : urlProvider.getUrl(remoteImages.get(position)),
                    imageLoader, maxImageWidth, maxImageHeight);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).imageView.clear();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return loading && position == getItemCount() - 1 ? VIEW_TYPE_FOOTER : VIEW_TYPE_IMAGE;
    }

    @Override
    public int getItemCount() {
        return remoteImages.size() + (loading ? 1 : 0);
    }

    private static ImageView createImageView(Context context, int drawableResId) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(drawableResId);
        return imageView;
    }
}

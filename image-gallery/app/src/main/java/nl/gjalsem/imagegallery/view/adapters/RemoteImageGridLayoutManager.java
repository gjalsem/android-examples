package nl.gjalsem.imagegallery.view.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import nl.gjalsem.imagegallery.R;

/**
 * A GridLayoutManager for use with RemoteImageRecyclerViewAdapter, with a span count based on a
 * minimum column width, and with room for a full width footer.
 */
public class RemoteImageGridLayoutManager extends GridLayoutManager {
    private final int minImageWidth;

    public RemoteImageGridLayoutManager(Context context, RemoteImageRecyclerViewAdapter adapter) {
        super(context, 1);

        minImageWidth = context.getResources().getDimensionPixelSize(R.dimen.grid_image_min_width);

        setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isFooter(position) ? getSpanCount() : 1;
            }
        });
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        setSpanCount(getWidth() / minImageWidth);
        super.onLayoutChildren(recycler, state);
    }
}

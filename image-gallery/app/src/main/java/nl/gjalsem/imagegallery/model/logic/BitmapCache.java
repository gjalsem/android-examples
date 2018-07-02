package nl.gjalsem.imagegallery.model.logic;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;

/**
 * An implementation of ImageLoader's ImageCache using a basic LruCache based on bitmap byte count.
 */
public class BitmapCache implements ImageLoader.ImageCache {
    private static final String TAG = BitmapCache.class.getSimpleName();
    private static final int MAX_CACHE_SIZE_KB = 1024 * 50;

    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(
            getBitmapCacheSizeKb()) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getByteCount() / 1024;
        }
    };

    @Override
    public Bitmap getBitmap(String key) {
        Bitmap bitmap = cache.get(key);
        if (bitmap == null) {
            Log.d(TAG, "Bitmap not in cache: " + key);
        }
        return bitmap;
    }

    @Override
    public void putBitmap(String key, Bitmap bitmap) {
        cache.put(key, bitmap);
    }

    private static int getBitmapCacheSizeKb() {
        int cacheSizeKb = Math.min(MAX_CACHE_SIZE_KB,
                (int) (Runtime.getRuntime().maxMemory() / 1024 / 8));
        Log.d(TAG, "Using " + cacheSizeKb + " KB for bitmap cache.");
        return cacheSizeKb;
    }
}

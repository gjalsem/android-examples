package nl.gjalsem.basickotlinexample.model

import android.graphics.Bitmap
import android.support.v4.util.LruCache
import com.android.volley.toolbox.ImageLoader

/**
 * Simple implementation of ImageLoader.ImageCache using LruCache based on bitmap size.
 */
class BitmapCache : ImageLoader.ImageCache {
    private val cache = object : LruCache<String, Bitmap>(1024 * 5) {
        override fun sizeOf(key: String?, value: Bitmap?): Int {
            return value?.let { it.byteCount / 1024 } ?: 0
        }
    }

    override fun getBitmap(url: String?): Bitmap? {
        return cache.get(url)
    }

    override fun putBitmap(url: String?, bitmap: Bitmap?) {
        cache.put(url, bitmap)
    }
}

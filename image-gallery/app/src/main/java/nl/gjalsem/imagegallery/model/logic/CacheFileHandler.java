package nl.gjalsem.imagegallery.model.logic;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Helper class that can read and write a cache file asynchronously.
 */
public class CacheFileHandler {
    public interface OnDataLoadedCallback {
        void onCacheLoaded(byte[] data);
    }

    private static class LoadTask extends AsyncTask<Void, Void, byte[]> {
        private final CacheFileHandler cachedData;
        private final OnDataLoadedCallback callback;

        private LoadTask(CacheFileHandler cachedData, OnDataLoadedCallback callback) {
            this.cachedData = cachedData;
            this.callback = callback;
        }

        @Override
        protected byte[] doInBackground(Void... v) {
            return cachedData.load();
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            if (!cachedData.canceled) {
                callback.onCacheLoaded(bytes);
            }
        }
    }

    private static class SaveTask extends AsyncTask<byte[], Void, Void> {
        private final CacheFileHandler cachedData;
        private final Runnable callback;

        private SaveTask(CacheFileHandler cachedData, Runnable callback) {
            this.cachedData = cachedData;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(byte[]... data) {
            cachedData.save(data[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (!cachedData.canceled) {
                callback.run();
            }
        }
    }

    private static final String TAG = CacheFileHandler.class.getSimpleName();

    private final Context context;
    private final String fileName;
    private final long timeout;

    private boolean canceled;

    public CacheFileHandler(Context context, String fileName, long timeout) {
        this.context = context.getApplicationContext();
        this.fileName = fileName;
        this.timeout = timeout;
    }

    public void load(OnDataLoadedCallback callback) {
        new LoadTask(this, callback).execute();
    }

    public void save(byte[] data, Runnable callback) {
        new SaveTask(this, callback).execute(data);
    }

    public void cancel() {
        canceled = true;
    }

    private byte[] load() {
        File file = new File(context.getCacheDir(), fileName);
        if (!file.exists()) {
            return null;
        }

        long lastModified = file.lastModified();
        long now = System.currentTimeMillis();
        if (lastModified < now - timeout || lastModified > now) {
            return null;
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];
            int bytesRead;
            while ((bytesRead = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            Log.w(TAG, e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.w(TAG, e);
                }
            }
        }
    }

    private void save(byte[] data) {
        File file = new File(context.getCacheDir(), fileName);

        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            Log.w(TAG, e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.w(TAG, e);
                }
            }
        }
    }
}

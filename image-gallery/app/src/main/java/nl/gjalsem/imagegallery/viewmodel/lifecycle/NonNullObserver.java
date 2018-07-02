package nl.gjalsem.imagegallery.viewmodel.lifecycle;

import android.support.annotation.NonNull;

/**
 * The same as LiveData's observer, only with a non-null value.
 */
public interface NonNullObserver<T> {
    void onDataChanged(@NonNull T value);
}

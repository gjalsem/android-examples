package nl.gjalsem.imagegallery.viewmodel.lifecycle;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

/**
 * A version of LiveData with a non-null value.
 */
public abstract class NonNullLiveData<T> {
    private final LiveData<T> liveData;

    NonNullLiveData(LiveData<T> liveData) {
        this.liveData = liveData;
    }

    @NonNull
    public T getValue() {
        T value = liveData.getValue();
        if (value == null) {
            throw new IllegalStateException("LeanLiveData value should never be null");
        }
        return value;
    }

    public void observe(LifecycleOwner owner, NonNullObserver<T> observer) {
        liveData.observe(owner, observer::onDataChanged);
    }

    public void removeObservers(LifecycleOwner owner) {
        liveData.removeObservers(owner);
    }
}

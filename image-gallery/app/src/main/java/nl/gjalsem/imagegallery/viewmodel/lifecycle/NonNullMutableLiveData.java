package nl.gjalsem.imagegallery.viewmodel.lifecycle;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

/**
 * Mutable version of NonNullLiveData, similar to MutableLiveData.
 */
public class NonNullMutableLiveData<T> extends NonNullLiveData<T> {
    private final MutableLiveData<T> mutableLiveData;

    public NonNullMutableLiveData(@NonNull T initialValue) {
        this(new MutableLiveData<>());
        mutableLiveData.setValue(initialValue);
    }

    private NonNullMutableLiveData(MutableLiveData<T> mutableLiveData) {
        super(mutableLiveData);
        this.mutableLiveData = mutableLiveData;
    }

    public void setValue(@NonNull T value) {
        mutableLiveData.setValue(value);
    }
}

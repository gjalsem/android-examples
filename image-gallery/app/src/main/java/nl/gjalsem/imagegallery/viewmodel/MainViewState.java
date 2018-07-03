package nl.gjalsem.imagegallery.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import nl.gjalsem.imagegallery.model.data.RemoteImage;

/**
 * The immutable state of the app.
 */
public class MainViewState implements Parcelable {
    public static class Builder {
        private List<RemoteImage> remoteImages;
        private boolean loading;
        private int nextPage;
        private String error;
        private int selectedImagePosition;

        Builder() {
            remoteImages = Collections.emptyList();
        }

        private Builder(MainViewState instance) {
            remoteImages = instance.remoteImages;
            loading = instance.loading;
            nextPage = instance.nextPage;
            error = instance.error;
            selectedImagePosition = instance.selectedImagePosition;
        }

        public Builder setRemoteImages(@NonNull List<RemoteImage> remoteImages) {
            this.remoteImages = remoteImages;
            return this;
        }

        public Builder setLoading(boolean loading) {
            this.loading = loading;
            return this;
        }

        public Builder setNextPage(int nextPage) {
            this.nextPage = nextPage;
            return this;
        }

        public Builder setError(String error) {
            this.error = error;
            return this;
        }

        public Builder setSelectedImagePosition(int selectedImagePosition) {
            this.selectedImagePosition = selectedImagePosition;
            return this;
        }

        public MainViewState build() {
            return new MainViewState(this);
        }
    }

    private final List<RemoteImage> remoteImages;
    private final boolean loading;
    private final int nextPage;
    private final String error;
    private final int selectedImagePosition;

    private MainViewState(Builder builder) {
        remoteImages = builder.remoteImages;
        loading = builder.loading;
        nextPage = builder.nextPage;
        error = builder.error;
        selectedImagePosition = builder.selectedImagePosition;
    }

    private MainViewState(Parcel in) {
        remoteImages = in.createTypedArrayList(RemoteImage.CREATOR);
        loading = in.readByte() != 0;
        nextPage = in.readInt();
        error = in.readString();
        selectedImagePosition = in.readInt();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @NonNull
    public List<RemoteImage> getRemoteImages() {
        return remoteImages;
    }

    public boolean isLoading() {
        return loading;
    }

    public int getNextPage() {
        return nextPage;
    }

    public String getError() {
        return error;
    }

    public int getSelectedImagePosition() {
        return selectedImagePosition;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(remoteImages);
        dest.writeByte((byte) (loading ? 1 : 0));
        dest.writeInt(nextPage);
        dest.writeString(error);
        dest.writeInt(selectedImagePosition);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MainViewState> CREATOR = new Creator<MainViewState>() {
        @Override
        public MainViewState createFromParcel(Parcel in) {
            return new MainViewState(in);
        }

        @Override
        public MainViewState[] newArray(int size) {
            return new MainViewState[size];
        }
    };
}

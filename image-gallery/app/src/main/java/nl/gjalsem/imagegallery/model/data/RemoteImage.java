package nl.gjalsem.imagegallery.model.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a remote image, with a thumbnail and full size URL.
 */
public class RemoteImage implements Parcelable {
    private final String thumbnailUrl;
    private final String fullSizeUrl;

    public RemoteImage(String thumbnailUrl, String fullSizeUrl) {
        this.thumbnailUrl = thumbnailUrl;
        this.fullSizeUrl = fullSizeUrl;
    }

    private RemoteImage(Parcel in) {
        thumbnailUrl = in.readString();
        fullSizeUrl = in.readString();
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getFullSizeUrl() {
        return fullSizeUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RemoteImage that = (RemoteImage) o;
        return thumbnailUrl.equals(that.thumbnailUrl) && fullSizeUrl.equals(that.fullSizeUrl);
    }

    @Override
    public int hashCode() {
        int result = thumbnailUrl.hashCode();
        result = 31 * result + fullSizeUrl.hashCode();
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumbnailUrl);
        dest.writeString(fullSizeUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RemoteImage> CREATOR = new Creator<RemoteImage>() {
        @Override
        public RemoteImage createFromParcel(Parcel in) {
            return new RemoteImage(in);
        }

        @Override
        public RemoteImage[] newArray(int size) {
            return new RemoteImage[size];
        }
    };
}

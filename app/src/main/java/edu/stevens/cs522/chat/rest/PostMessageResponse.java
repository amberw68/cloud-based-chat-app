package edu.stevens.cs522.chat.rest;

import android.net.Uri;
import android.os.Parcel;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by dduggan.
 */

public class PostMessageResponse extends Response {

    private final static String LOCATION = "Location";

    public long messageId;

    public PostMessageResponse(HttpURLConnection connection) throws IOException {
        super(connection);
        String location = connection.getHeaderField(LOCATION);
        if (location != null) {
            Uri uri = Uri.parse(location);
            messageId = Long.parseLong((uri.getLastPathSegment()));
        }
    }

    @Override
    public boolean isValid() { return true; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        super.writeToParcel(dest,flags);
    }

    public PostMessageResponse(Parcel in) {
        super(in);
        // TODO
    }

    public static Creator<PostMessageResponse> CREATOR = new Creator<PostMessageResponse>() {
        @Override
        public PostMessageResponse createFromParcel(Parcel source) {
            return new PostMessageResponse(source);
        }

        @Override
        public PostMessageResponse[] newArray(int size) {
            return new PostMessageResponse[size];
        }
    };
}

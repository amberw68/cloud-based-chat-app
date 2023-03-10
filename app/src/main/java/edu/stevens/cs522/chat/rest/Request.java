package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.DateUtils;
import edu.stevens.cs522.chat.util.EnumUtils;

/**
 * Created by dduggan.
 */

public abstract class Request implements Parcelable {


    public static enum RequestType {
        REGISTER("Register"),
        POST_MESSAGE("Post Message"),
        SYNCHRONIZE("Synchronize");
        private String value;
        private RequestType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    public static enum StatusType {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        FAILED("Failed"),
        COMPLETED("Completed");
        private String value;
        private StatusType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    // Sender senderId (database key)
    public long senderId;

    public String chatName;

    // Status of a request
    public StatusType status;

    // Installation id
    public UUID clientID;

    // App version
    public long version;

    // Time stamp
    public Date timestamp;

    // Device coordinates
    public double longitude;

    public double latitude;



    public Request() {
        this.timestamp = DateUtils.now();
    }

    protected Request(long senderId, UUID clientID) {
        this.senderId = senderId;
        this.clientID = clientID;
    }


    protected Request(String chatName, UUID clientID) {
        this.status = StatusType.PENDING;
        this.timestamp = DateUtils.now();
        this.chatName = chatName;
        this.clientID = clientID;
    }




    protected Request(Parcel in) {
        // TODO assume tag has already been read, this will be called by subclass constructor

    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // TODO subclasses write tag, then call this, then write out their own fields
    }

    /*
     * HTTP request headers
     */

    public static String CLIENT_ID_HEADER = "X-Client-Id";

    public static String TIMESTAMP_HEADER = "X-Timestamp";

    public static String LONGITUDE_HEADER = "X-Longitude";

    public static String LATITUDE_HEADER = "X-Latitude";

    // App-specific HTTP request headers.
    public Map<String,String> getRequestHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put(CLIENT_ID_HEADER, this.clientID.toString());
        headers.put(TIMESTAMP_HEADER, Long.toString(timestamp.getTime()));
        headers.put(LONGITUDE_HEADER, Double.toString(longitude));
        headers.put(LATITUDE_HEADER, Double.toString(latitude));
        return headers;
    }

    // JSON body (if not null) for request data not passed in headers.
    public abstract String getRequestEntity() throws IOException;

    // Define your own Response class, including HTTP response code.
    public abstract Response getResponse(HttpURLConnection connection,
                                         JsonReader rd /* Null for streaming */)
            throws IOException;

    public final Response getResponse(HttpURLConnection connection) throws IOException {
        return getResponse(connection, null);
    }

    public abstract Response process(RequestProcessor processor);

    public int describeContents() {
        return 0;
    }

    public static Request createRequest(Parcel in) {
        RequestType requestType = EnumUtils.readEnum(RequestType.class, in);
        switch (requestType) {
            case REGISTER:
                return new RegisterRequest(in);
            case POST_MESSAGE:
                return new PostMessageRequest(in);
            case SYNCHRONIZE:
                return new SynchronizeRequest(in);
            default:
                break;
        }
        throw new IllegalArgumentException("Unknown request type: "+requestType.name());
    }

    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
        public Request createFromParcel(Parcel in) {
            return createRequest(in);
        }

        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

}
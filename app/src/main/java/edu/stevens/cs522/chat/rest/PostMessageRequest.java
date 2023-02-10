package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.DateUtils;

/**
 * Created by dduggan.
 */

public class PostMessageRequest extends Request {

    public String TAG = "chat.PostMessageRequest";

    public ChatMessage chatMessage;


    public PostMessageRequest(ChatMessage message) {
        super();
        this.chatMessage = message;

    }

    /*
     * HTTP request headers
     */
    public static String CLIENT_ID_HEADER = "X-Client-Id";

    public static String TIMESTAMP_HEADER = "X-Timestamp";

    public static String LONGITUDE_HEADER = "X-Longitude";

    public static String LATITUDE_HEADER = "X-Latitude";

    @Override
    public Map<String, String> getRequestHeaders() {

  // TODO
        return super.getRequestHeaders();
    }



    @Override
    public String getRequestEntity() throws IOException {
        StringWriter wr = new StringWriter();
        JsonWriter jw = new JsonWriter(wr);
        // TODO write a JSON message of the form:
        jw.beginObject();
        jw.name("chatroom").value(this.chatMessage.chatRoom);
        jw.name("text").value(this.chatMessage.messageText);
        jw.endObject();
        jw.flush();
        return wr.toString();
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        return new PostMessageResponse(connection);
    }

    public Response getDummyResponse() {
        return new DummyResponse();
    }

    @Override
    public Response process(RequestProcessor processor) {
        return processor.perform(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        dest.writeParcelable(this.chatMessage, flags);
    }

    public PostMessageRequest(Parcel in) {
        super(in);
        // TODO

        this.chatMessage = (ChatMessage) in.readParcelable(ChatMessage.class.getClassLoader());
    }

    public static Creator<PostMessageRequest> CREATOR = new Creator<PostMessageRequest>() {
        @Override
        public PostMessageRequest createFromParcel(Parcel source) {
            return new PostMessageRequest(source);
        }

        @Override
        public PostMessageRequest[] newArray(int size) {
            return new PostMessageRequest[size];
        }
    };

}

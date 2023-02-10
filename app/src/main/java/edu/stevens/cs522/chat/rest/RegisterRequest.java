package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.util.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dduggan.
 */

public class RegisterRequest extends Request {

    public String chatName;

    public UUID clientId;

    public RegisterRequest(String chatname, UUID clientID) {
        super(chatname, clientID);
        this.chatName = chatname;
        this.clientId = clientID;
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        // TODO add headers
        return super.getRequestHeaders();
    }

    @Override
    public String getRequestEntity() throws IOException {
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        return new RegisterResponse(connection);
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

        dest.writeString(this.chatName);
        dest.writeSerializable(this.clientID);
        dest.writeSerializable(this.timestamp);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
    }

    public RegisterRequest(Parcel in) {
        super(in);
        // TODO
        this.chatName = in.readString();
        this.clientID = (UUID) in.readSerializable();
        this.timestamp = (Date) in.readSerializable();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
    }

    public static Creator<RegisterRequest> CREATOR = new Creator<RegisterRequest>() {
        @Override
        public RegisterRequest createFromParcel(Parcel source) {
            return new RegisterRequest(source);
        }

        @Override
        public RegisterRequest[] newArray(int size) {
            return new RegisterRequest[size];
        }
    };

}

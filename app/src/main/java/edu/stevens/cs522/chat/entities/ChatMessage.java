package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable {

    // Primary key in the database
    public long id;

    // Global id provided by the server
    public long seqNum;

    public String messageText;

    public String chatRoom;

    // When and where the message was sent
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    // Sender username and FK (in local database)
    public String sender;

    public long senderId;

    public ChatMessage() {
        this.seqNum = 0;
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public ChatMessage(Cursor cursor) {
        // TODO
        this.id = Long.parseLong(MessageContract.getId(cursor));
        this.seqNum = MessageContract.getSequenceNumber(cursor);
        this.chatRoom = MessageContract.getChatRoom(cursor);
        this.sender = MessageContract.getSender(cursor);
        //this.senderId = MessageContract.getSenderId(cursor);
        this.messageText = MessageContract.getMessageText(cursor);
        this.timestamp = MessageContract.getTimestamp(cursor);
        this.longitude = MessageContract.getLongitude(cursor);
        this.latitude = MessageContract.getLatitude(cursor);
    }

    protected ChatMessage(Parcel in) {
        id = in.readLong();
        seqNum = in.readLong();
        chatRoom = in.readString();
        sender = in.readString();
        //senderId = in.readLong();
        messageText = in.readString();
        timestamp = new Date(in.readLong());
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    public void writeToProvider(ContentValues values) {
        // TODO
        MessageContract.putSequenceNumberColumn(values, seqNum);
        MessageContract.putChatRoom(values, chatRoom);
        MessageContract.putSender(values, sender);
        //MessageContract.putSenderId(values, senderId);
        MessageContract.putTimestamp(values, timestamp);
        MessageContract.putMessageText(values, messageText);
        MessageContract.putLongitude(values, longitude);
        MessageContract.putLatitude(values, latitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(seqNum);
        parcel.writeString(chatRoom);
        parcel.writeString(sender);
        //parcel.writeLong(senderId);
        parcel.writeString(messageText);
        parcel.writeLong(timestamp.getTime());
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
    }

    public String print() {
        return "Message:::" + " id=" + id + " +seqNum=" + seqNum + " +messageText=" + messageText
                        + " +chatRoom=" + chatRoom + " +timestamp=" + timestamp + " +sender=" + sender
                        + " +senderId=" + senderId + " +longitude=" + longitude + " +latitude= " + latitude;
    }

}

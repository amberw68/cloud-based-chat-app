package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.Date;

/**
 * Created by dduggan.
 */

public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(BaseContract.AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));

    public static final String CONTENT_TYPE = "vnd.android.cursor/vnd."
            + AUTHORITY + "."
            + "messages";

    public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "."
            + "message";

    private static final Uri CONTENT_URI_SYNC = withExtendedPath(CONTENT_URI, "sync");

    public static final Uri CONTENT_URI_SYNC(int id) {
        return CONTENT_URI_SYNC(Integer.toString(id));
    }

    private static final Uri CONTENT_URI_SYNC(String id) {
        return withExtendedPath(CONTENT_URI_SYNC, id);
    }

    public static final String CONTENT_PATH_SYNC = CONTENT_PATH(CONTENT_URI_SYNC("#"));


    public static final String ID = _ID;

    public static final String SEQUENCE_NUMBER = "sequence_number";

    public static final String MESSAGE_TEXT = "message_text";

    public static final String TIMESTAMP = "timestamp";

    public static final String SENDER = "sender";

    public static final String CHAT_ROOM = "chat_room";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String SENDER_ID = "peer_fk";

    public static final String[] COLUMNS = {ID, SEQUENCE_NUMBER, MESSAGE_TEXT, CHAT_ROOM, TIMESTAMP, LATITUDE, LONGITUDE, SENDER};

    // TODO remaining columns in Messages table

    private static int messageTextColumn = -1;

    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }

    private static int sequenceNumberColumn = -1;

    public static long getSequenceNumber(Cursor cursor) {
        if (sequenceNumberColumn < 0) {
            sequenceNumberColumn = cursor.getColumnIndexOrThrow(SEQUENCE_NUMBER);
        }
        return Long.valueOf(cursor.getString(sequenceNumberColumn));
    }

    public static void putSequenceNumberColumn(ContentValues out, long seqnum) {
        out.put(SEQUENCE_NUMBER, seqnum);
    }

    // TODO remaining getter and putter operations for other columns

    public static String getId(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(ID));
    }

    public static void putId(ContentValues values, String id) {
        values.put(ID, id);
    }

    public static String getSender(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(SENDER));
    }

    public static void putSender(ContentValues values, String sender) {
        values.put(SENDER, sender);
    }

    public static long getSenderId(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(SENDER_ID));
    }

    public static void putSenderId(ContentValues values, Long senderId) {
        values.put(SENDER_ID, senderId);
    }

    public static Date getTimestamp(Cursor cursor) {
        return new Date(cursor.getLong(cursor.getColumnIndexOrThrow(TIMESTAMP)));
    }

    public static void putTimestamp(ContentValues values, Date timeStamp) {
        if(timeStamp == null) {
            timeStamp = new Date();
        }
        values.put(TIMESTAMP, timeStamp.toString());
    }

    public static Double getLatitude(Cursor cursor) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(LATITUDE));
    }

    public static void putLatitude(ContentValues values, Double latitude) {
        values.put(LATITUDE,latitude);
    }

    public static Double getLongitude(Cursor cursor) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(LONGITUDE));
    }

    public static void putLongitude(ContentValues values, Double longitude) {
        values.put(LONGITUDE,longitude);
    }

    public static String getChatRoom(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(CHAT_ROOM));
    }

    public static void putChatRoom(ContentValues values, String chatRoom){
        values.put(CHAT_ROOM, chatRoom);
    }
}
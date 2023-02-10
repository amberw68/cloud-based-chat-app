package edu.stevens.cs522.chat.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.UUID;
import java.util.prefs.Preferences;

/**
 * Created by dduggan.
 */

public class Settings {

    /**
     * This controls whether we are posting messages one at a time to server
     * or periodically synchronizing the local message database with the server.
     */
    public static final boolean SYNC = true;

    public static final String SETTINGS = "settings";

    private static final String REGISTERED_KEY = "registered";

    private static String CLIENT_ID_KEY = "client-id";

    private static String SENDER_ID_KEY = "sender-id";

    private static final String CHAT_NAME_KEY = "user-name";

    public static UUID getClientId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        String clientID = prefs.getString(CLIENT_ID_KEY, null);
        if (clientID == null) {
            clientID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(CLIENT_ID_KEY, clientID);
            editor.commit();
        }
        return UUID.fromString(clientID);
    }

    public static long getSenderId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return prefs.getLong(SENDER_ID_KEY, 0);
    }

    public static void saveSenderId(Context context, long senderId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putLong(SENDER_ID_KEY, senderId);
        editor.commit();
    }

    public static String getChatName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(CHAT_NAME_KEY, null);
    }

    public static void saveChatName(Context context, String chatName) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit();
        //SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(CHAT_NAME_KEY, chatName);
        editor.commit();
    }

    public static boolean isRegistered(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return prefs.getBoolean(REGISTERED_KEY, false);
    }

    public static void setRegistered(Context context, boolean init) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit();
        // SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(REGISTERED_KEY, init);
        editor.commit();
    }

}

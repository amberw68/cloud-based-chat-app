/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.rest.ServiceManager;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.InetAddressUtils;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

public class ChatActivity extends Activity implements OnClickListener, QueryBuilder.IQueryListener<ChatMessage>, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();
		
    /*
     * UI for displaying received messages
     */
	private SimpleCursorAdapter messages;
	
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    private ServiceManager serviceManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText chatRoomName;

    private EditText messageText;

    private Button sendButton;

    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when message is sent.
     */
    public ResultReceiverWrapper sendResultReceiver;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.messages);

        messageList = (ListView) findViewById(R.id.message_list);
        chatRoomName = (EditText) findViewById(R.id.chat_room);
        messageText = (EditText) findViewById(R.id.message_text);
        sendButton = (Button) findViewById(R.id.send_button);

        // TODO use SimpleCursorAdapter to display the messages received.
        String[] from = {MessageContract.SENDER, MessageContract.MESSAGE_TEXT};
        int [] to = {R.id.sender, R.id.message};
        messagesAdapter = new SimpleCursorAdapter(this, R.layout.message, null, from, to,0);
        messageList.setAdapter(messagesAdapter);

        // TODO create the message and peer managers, and initiate a query for all messages
        peerManager = new PeerManager(this);
        messageManager = new MessageManager(this);
        messageManager.getAllMessagesAsync(this);

        // TODO instantiate helper for service
        helper = new ChatHelper(this);

        // TODO initialize sendResultReceiver
        sendResultReceiver = new ResultReceiverWrapper(new Handler());

        // TODO (SYNC) initialize serviceManager
        serviceManager = new ServiceManager(this);

        sendButton.setOnClickListener(this);

        /**
         * Initialize settings to default values.
         */
        if (!Settings.isRegistered(this)) {
            // TODO launch registration activity
            Settings.getClientId(this);
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }

	public void onResume() {
        super.onResume();
        sendResultReceiver.setReceiver(this);
        if (Settings.SYNC) {
            serviceManager.scheduleBackgroundOperations();
        }
    }

    public void onPause() {
        super.onPause();
        sendResultReceiver.setReceiver(null);
        if (Settings.SYNC) {
            serviceManager.cancelBackgroundOperations();
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS and SETTINGS options
        getMenuInflater().inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent;
        switch(item.getItemId()) {

            // TODO PEERS provide the UI for viewing list of peers
            case R.id.peers:
                intent = new Intent(this, ViewPeersActivity.class);
                startActivity(intent);
                break;

            // TODO PEERS provide the UI for registering
            case R.id.register:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;

            // TODO SETTINGS provide the UI for settings
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return false;
    }



    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {
        if (helper != null) {

            String chatRoom;

            String message = null;

            // TODO get chatRoom and message from UI, and use helper to post a message
            // TODO add the message to the database
            chatRoom = chatRoomName.getText().toString();
            message = messageText.getText().toString();
            helper.postMessage(chatRoom, message, sendResultReceiver);


            // End todo


            messageText.setText("");
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case RESULT_OK:
                // TODO show a success toast message
                Toast.makeText(this, "Successful!", Toast.LENGTH_LONG).show();
                break;
            default:
                // TODO show a failure toast message
                Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void handleResults(TypedCursor<ChatMessage> results) {
        // TODO
        //messagesAdapter.swapCursor(results.getCursor());
        Cursor cursor = results.getCursor();
        messagesAdapter.swapCursor(cursor);
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void closeResults() {
        // TODO
        messagesAdapter.swapCursor(null);
    }

}
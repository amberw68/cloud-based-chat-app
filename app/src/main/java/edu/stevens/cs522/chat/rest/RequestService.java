package edu.stevens.cs522.chat.rest;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;
import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_SEND;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RequestService extends IntentService {


    public static final String SERVICE_REQUEST_KEY = "edu.stevens.cs522.chat.rest.extra.REQUEST";

    public static final String RESULT_RECEIVER_KEY = "edu.stevens.cs522.chat.rest.extra.RECEIVER";


    private RequestProcessor processor;

    public RequestService() {
        super("RequestService");
    }

    @Override
    public void onCreate() {
        processor = new RequestProcessor(this);
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Request request = intent.getParcelableExtra(SERVICE_REQUEST_KEY);
        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER_KEY);
        if (request == null) return;

        Response response = processor.process(request);

        if (receiver != null) {
            // TODO use receiver to call back to activity
            if(response.getClass().getName().equals("edu.stevens.cs522.chat.rest.PostMessageResponse")){
                PostMessageResponse postMessageResponse = (PostMessageResponse) response;
                Bundle bundle = new Bundle();
                bundle.putInt("MESSAGE_SEQUENCE_NUMBER", (int)postMessageResponse.messageId);
                receiver.send(Activity.RESULT_OK, bundle);
                return;
            }
            receiver.send(RESULT_OK, null);
            Toast.makeText(this, "Request Successfully Handled.", Toast.LENGTH_LONG).show();
        }
    }

}

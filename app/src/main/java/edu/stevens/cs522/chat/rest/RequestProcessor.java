package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.net.Uri;
import android.os.ResultReceiver;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.RequestManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.DateUtils;
import edu.stevens.cs522.chat.util.StringUtils;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    public static String TAG = "chat.RequestProcessor";

    private Context context;

    private RestMethod restMethod;

    private RequestManager requestManager;

    public String getUsernameOfCurrentID(long id, List<Peer> peers) {
        for(Peer curr : peers) {
            if(curr.id == id) {
                return curr.name;
            }
        }
        return "default";
    }

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod = new RestMethod(context);
        // Used for SYNC
        this.requestManager = new RequestManager(context);
    }

    public Response process(Request request) {
        return request.process(this);
    }

    public Response perform(RegisterRequest request) {
        Response response = restMethod.perform(request);
        if (response instanceof RegisterResponse) {
            // TODO update the sender senderId in settings, updated peer record PK
        }
        return response;
    }

    public Response perform(PostMessageRequest request) {
        if (!Settings.SYNC) {
            // TODO insert the message into the local database

            Response response = restMethod.perform(request);
            if (response instanceof PostMessageResponse) {
                // TODO update the message in the database with the sequence number

            }
            return response;
        } else {
            /*
             * We will just insert the message into the database, and rely on background
             * sync to upload.
             */
            ChatMessage chatMessage = request.chatMessage;
            // TODO fill the fields with values from the request message

            requestManager.persist(chatMessage);
            return request.getDummyResponse();
        }
    }

    /**
     * For SYNC: perform a sync using a request manager
     * @param request
     * @return
     */
    public Response perform(final SynchronizeRequest request) {
        RestMethod.StreamingResponse response = null;
        final TypedCursor<ChatMessage> messages = requestManager.getUnsentMessages();
        request.lastSequenceNumber = requestManager.getLastSequenceNumber();
        //Log.i(TAG, "*****************lastSequenceNumber****************" + request.lastSequenceNumber);
        request.timestamp = new Date();
        request.latitude = 0.0;
        request.longitude = 0.0;
        int numMessagesReplaced = messages.getCount();
        if (numMessagesReplaced < 1) {
            return new DummyResponse();
        }
        try {
            /*
             * This is the callback from streaming new local messages to the server.
             */
            RestMethod.StreamingOutput out = new RestMethod.StreamingOutput() {
                @Override
                public void write(final OutputStream os) throws IOException {
                    try {
                        JsonWriter wr = new JsonWriter(new OutputStreamWriter(new BufferedOutputStream(os)));
                        wr.beginArray();
                        /*
                         * TODO stream unread messages to the server:
                         * {
                         *   chatroom : ...,
                         *   timestamp : ...,
                         *   latitude : ...,
                         *   longitude : ....,
                         *   text : ...
                         * }
                         */
                        while (messages.moveToNext()) {
                            ChatMessage message = new ChatMessage(messages.getCursor());
                            //Log.i(TAG, "******************beforeMessage************* + message.print());
                            wr.beginObject();
                            wr.name("chatroom");
                            wr.value(message.chatRoom);
                            wr.name("timestamp");
                            wr.value(message.timestamp.getTime());
                            wr.name("latitude");
                            wr.value(message.latitude);
                            wr.name("longitude");
                            wr.value(message.longitude);
                            wr.name("text");
                            wr.value(message.messageText);
                            wr.endObject();
                        }
                        wr.endArray();
                        wr.flush();
                    } finally {
                        messages.close();
                    }
                }
            };
            /*
             * Connect to the server and upload messages not yet shared.
             */
            response = restMethod.perform(request, out);

            /*
             * Stream downloaded peer and message information, and update the database.
             * The connection is closed in the finally block below.
             */
            JsonReader rd = new JsonReader(new InputStreamReader(new BufferedInputStream(response.getInputStream()), StringUtils.CHARSET));
            // TODO parse data from server (messages and peers) and update database
            List<ChatMessage> downloadedMessages = new LinkedList<>();
            List<Peer> downloadedPeers = new ArrayList<>();
            rd.beginObject();
            Log.i(TAG, "json read - server+++++++++++++++++");
            while (rd.hasNext()) {
                String nextName = rd.nextName();
                if (nextName.equals("clients")){
                    rd.beginArray();
                    while(rd.hasNext()){
                        Peer peer = new Peer();
                        rd.beginObject();
                        while(rd.hasNext()){
                            switch (rd.nextName()){
                                case "id":
                                    peer.id = rd.nextLong();
                                    Log.i(TAG, "++++++id====" + peer.id);
                                    break;
                                case "username":
                                    peer.name = rd.nextString();
                                    Log.i(TAG, "++++++++name====" + peer.name);
                                    break;
                                case "timestamp":
                                    peer.timestamp = new Date(rd.nextLong());
                                    Log.i(TAG, "++++++++timestamp===" + peer.timestamp);
                                    break;
                                case "latitude":
                                    peer.latitude = rd.nextDouble();
                                    Log.i(TAG, "+++++++++++++++++latitude===" + peer.latitude);
                                    break;
                                case "longitude":
                                    peer.longitude = rd.nextDouble();
                                    Log.i(TAG, "++++++++++++++++++longitide===" + peer.longitude);
                                    break;
                            }
                        }
                        rd.endObject();
                        downloadedPeers.add(peer);
                    }
                    rd.endArray();
                }
                else if (nextName.equals("messages")) {
                    rd.beginArray();
                    while (rd.hasNext()) {
                        ChatMessage message = new ChatMessage();
                        rd.beginObject();
                        while (rd.hasNext()) {
                            switch (rd.nextName()) {
                                case "chatroom":
                                    message.chatRoom = rd.nextString();
                                    Log.i(TAG, "++++++++++++++chatRoom===" + message.chatRoom);
                                    break;
                                case "timestamp":
                                    message.timestamp = new Date(rd.nextLong());
                                    Log.i(TAG, "++++++++++++++timestamp===" + message.timestamp);
                                    break;
                                case "latitude":
                                    message.latitude = rd.nextDouble();
                                    Log.i(TAG, "++++++++++++++++++latitude===" + message.latitude);
                                    break;
                                case "longitude":
                                    message.longitude = rd.nextDouble();
                                    Log.i(TAG, "++++++++++++++++++longitude===" + message.longitude);
                                    break;
                                case "seqnum":
                                    message.seqNum = rd.nextInt();
                                    Log.i(TAG, "++++++++++++++++seqNum===" + message.seqNum);
                                    break;
                                case "sender":
                                    message.senderId = Integer.parseInt(rd.nextString());
                                    message.sender = getUsernameOfCurrentID(message.senderId, downloadedPeers);
                                    Log.i(TAG, "++++++++++++++++++sender===" + message.sender);
                                    break;
                                case "text":
                                    message.messageText = rd.nextString();
                                    Log.i(TAG, "++++++++++++messageText===" + message.messageText);
                                    break;
                            }
                        }
                        rd.endObject();
                        downloadedMessages.add(message);
                    }
                    rd.endArray();
                } else
                    rd.skipValue();
            }
            rd.endObject();
            requestManager.updateSeqNum(downloadedMessages.size(), downloadedMessages.size());
            requestManager.syncMessages(downloadedMessages.size(), downloadedMessages);
            requestManager.deletePeers();
            for(Peer peer: downloadedPeers){
                requestManager.persist(peer);
            }
            return response.getResponse();

        } catch (IOException e) {
            return new ErrorResponse(0, ErrorResponse.Status.SERVER_ERROR, e.getMessage());

        } finally {
            if (response != null) {
                response.disconnect();
            }
        }
    }


}

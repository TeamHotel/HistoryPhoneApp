package uk.ac.cam.teamhotel.historyphone.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.database.ChatMessage;
import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;
import uk.ac.cam.teamhotel.historyphone.server.MessageContainer;
import uk.ac.cam.teamhotel.historyphone.server.MessageSender;
import uk.ac.cam.teamhotel.historyphone.utils.TimeStampHelper;

public class ChatActivity extends AppCompatActivity {

    private static boolean ENABLE_CHAT = false;
    private static long uuid;
    private DatabaseHelper dbHelper;
    private ChatAdapter adapter;

    public static List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get artifact uuid passed from previous fragment view
        uuid = getIntent().getLongExtra("UUID",0L);
        ENABLE_CHAT = getIntent().getBooleanExtra("ENABLE_CHAT", false);

        //This is defined in the ChatActivity Class so only one instance is ever created (for efficiency) and so it can be accessed by the AsyncTask
        dbHelper = new DatabaseHelper(this);
        //load in messages from database
        loadChatListFromDB();

        //send 'init' message and server will reply with object greeting
        new MessageAsyncTask().execute(new MessageContainer("init", uuid));

        ListView chatMessages = (ListView) findViewById(R.id.chat_list);
        adapter = new ChatAdapter(getApplicationContext(),chatMessageList );
        chatMessages.setAdapter(adapter);


        //get the text field view
        EditText editText = (EditText) findViewById(R.id.enterText);
        //get the send button
        Button send_btn = (Button) findViewById(R.id.btn_Send);

        if(ENABLE_CHAT == false) {
            editText.setEnabled(false);
            send_btn.setEnabled(false);
        }


    }

    /**
     * Called when the user taps the send button.
     */
    public void sendMessage(View view) {
        //get the text field view
        EditText editText = (EditText) findViewById(R.id.enterText);

            //setup new ChatMessage object to store in db
            String message = editText.getText().toString();
            ChatMessage newMessage = new ChatMessage();
            newMessage.setMessage_text(message);
            newMessage.setFrom_user(true);
            newMessage.setTimestamp(TimeStampHelper.getTimeStamp());
            newMessage.setUuid(uuid);

            //add message to local db messages table
            dbHelper.addMessage(newMessage);

            //add or update the conversations table with the most recent timestamp for the current Artifact
            dbHelper.addToOrUpdateConversations(newMessage);

            chatMessageList.add(newMessage);

            //reset the text view to empty
            editText.setText("");

            dbHelper.printConversations();

            //Send message to server and receive reply.
            new MessageAsyncTask().execute(new MessageContainer(message, uuid));

    }

    /**
     * Called when loading a conversation.
     */
    public void loadChatListFromDB(){

        chatMessageList = dbHelper.returnAllMessages(uuid);

    }

    /**
     * This is used to send messages and receive responses - it assumes that messages have already been saved.
     */
    private class MessageAsyncTask extends AsyncTask<MessageContainer, Void, String> {

        @Override
        protected String doInBackground(MessageContainer... params) {
            //invoke static method to download artifact with uuid 123.
            String message = params[0].getMessage();
            long uuid = params[0].getUuid();
            return MessageSender.sendMessage(message, uuid);
        }

        @Override
        protected void onPostExecute(String reply) {
            if (reply != null) {
                ChatMessage newMessage = new ChatMessage();
                newMessage.setMessage_text(reply);
                newMessage.setFrom_user(false);
                newMessage.setTimestamp(TimeStampHelper.getTimeStamp());

                dbHelper.addMessage(newMessage );
                chatMessageList.add(newMessage);
                //update list of items displayed
                adapter.notifyDataSetChanged();
            } else { //no response received - i.e. no connection to server or error with response
                Snackbar.make(findViewById(R.id.chat_list), "You have lost connection", Snackbar.LENGTH_LONG).show();
            }
            super.onPostExecute(reply);
        }
    }

}

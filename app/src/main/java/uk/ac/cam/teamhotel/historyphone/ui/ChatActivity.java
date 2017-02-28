package uk.ac.cam.teamhotel.historyphone.ui;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.HistoryPhoneApplication;
import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.database.ChatMessage;
import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;
import uk.ac.cam.teamhotel.historyphone.server.MessageContainer;
import uk.ac.cam.teamhotel.historyphone.server.MessageSender;
import uk.ac.cam.teamhotel.historyphone.utils.TimeStampHelper;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "ChatActivity";

    private static long uuid;
    private DatabaseHelper databaseHelper;
    private ChatAdapter adapter;

    public static List<ChatMessage> chatMessageList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get artifact uuid passed from previous fragment view
        uuid = getIntent().getLongExtra("UUID", -1);
        boolean enableChat = getIntent().getBooleanExtra("ENABLE_CHAT", false);
        String name = getIntent().getStringExtra("NAME");
        String description = getIntent().getStringExtra("DESC");
        Log.d(TAG, name + ": " + uuid + " : " + description);

        // This is defined in the ChatActivity Class so only one instance is ever created
        // (for efficiency) and so it can be accessed by the AsyncTask.
        databaseHelper = ((HistoryPhoneApplication) getApplication()).getDatabaseHelper();

        // Load in messages from the database.
        loadChatListFromDB();

        // Send 'init' message and server will reply with object greeting (only if chat enabled).
        if (enableChat) {
            new MessageAsyncTask().execute(new MessageContainer("init", uuid));
        }

        ListView chatMessages = (ListView) findViewById(R.id.chat_list);
        adapter = new ChatAdapter(this, chatMessageList);
        chatMessages.setAdapter(adapter);


        EditText editText = (EditText) findViewById(R.id.enterText);
        Button sendButton = (Button) findViewById(R.id.btn_Send);

        // If fired from the recent tab, then we don't want the user to be able to chat.
        if (!enableChat) {
            editText.setEnabled(false);
            sendButton.setEnabled(false);
        }

        databaseHelper.printArtifacts();
        databaseHelper.printMessages();

    }

    /**
     * Called when the user taps the send button.
     */
    public void sendMessage(View view) {
        // Get the text field view
        EditText editText = (EditText) findViewById(R.id.enterText);

        // Setup new ChatMessage object to store in db
        String message = editText.getText().toString();
        ChatMessage newMessage = new ChatMessage();
        newMessage.setMessage_text(message);
        newMessage.setFrom_user(true);
        newMessage.setTimestamp(TimeStampHelper.getTimeStamp());
        newMessage.setUuid(uuid);

        // Add message to local db messages table
        databaseHelper.addMessage(newMessage);

        // Add or update the conversations table with the most recent
        // timestamp for the current Artifact.
        databaseHelper.addToOrUpdateConversations(newMessage);
        chatMessageList.add(newMessage);

        // Reset the text view to empty
        editText.setText("");

        // Send message to server and receive reply.
        new MessageAsyncTask().execute(new MessageContainer(message, uuid));
    }

    /**
     * Called when loading a conversation.
     */
    public void loadChatListFromDB(){
        chatMessageList = databaseHelper.returnAllMessages(uuid);
    }

    /**
     * This is used to send messages and receive responses - it assumes that messages have already been saved.
     */
    private class MessageAsyncTask extends AsyncTask<MessageContainer, Void, String> {

        @Override
        protected String doInBackground(MessageContainer... params) {
            // Invoke static method to download artifact with uuid 123.
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
                newMessage.setUuid(uuid);

                databaseHelper.addMessage(newMessage);
                chatMessageList.add(newMessage);
                // Update list of items displayed
                adapter.notifyDataSetChanged();
            } else {
                // No response received - i.e. no connection to server or error with response
                Snackbar.make(findViewById(R.id.chat_list), "You have lost connection",
                        Snackbar.LENGTH_LONG).show();
            }
            super.onPostExecute(reply);
        }
    }

}

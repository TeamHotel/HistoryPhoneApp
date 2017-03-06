package uk.ac.cam.teamhotel.historyphone.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.HistoryPhoneApplication;
import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.artifact.ArtifactLoader;
import uk.ac.cam.teamhotel.historyphone.database.ChatMessage;
import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;
import uk.ac.cam.teamhotel.historyphone.server.MessageContainer;
import uk.ac.cam.teamhotel.historyphone.server.MessageSender;
import uk.ac.cam.teamhotel.historyphone.utils.TimeStampHelper;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "ChatActivity";

    private Artifact artifact;
    private DatabaseHelper databaseHelper;
    private ChatAdapter adapter;
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get artifact UUID passed from previous fragment view.
        long uuid = getIntent().getLongExtra("UUID", -1);
        boolean enableChat = getIntent().getBooleanExtra("ENABLE_CHAT", false);

        // Get the database helper and artifact loader from the application instance.
        databaseHelper = ((HistoryPhoneApplication) getApplication()).getDatabaseHelper();
        ArtifactLoader artifactLoader =
                ((HistoryPhoneApplication) getApplication()).getArtifactLoader();
        artifact = artifactLoader.load(uuid);

        // Load in messages from the database.
        databaseHelper.printMessages();
        loadChatListFromDB();

        // Send 'init' message and server will reply with object greeting (only if chat enabled).
        if (enableChat) {
            new MessageAsyncTask().execute(new MessageContainer("init", uuid));
        }

        // Set up the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Log.e(TAG, "Actionbar is null.");
        } else {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(artifact.getName());
        }

        // Set up the adapter for the chat message list.
        ListView chatMessages = (ListView) findViewById(R.id.chat_list);
        adapter = new ChatAdapter(this, artifactLoader.load(uuid), chatMessageList);
        chatMessages.setAdapter(adapter);

        // Find the input views.
        EditText editText = (EditText) findViewById(R.id.enterText);
        Button sendButton = (Button) findViewById(R.id.btn_Send);

        // If fired from the recent tab, then we don't want the user to be able to chat.
        if (!enableChat) {
            editText.setEnabled(false);
            sendButton.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back button in action bar was tapped.
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        newMessage.setText(message);
        newMessage.setFromUser(true);
        newMessage.setTimestamp(TimeStampHelper.getTimeStamp());
        newMessage.setArtifactUUID(artifact.getUUID());

        // Add message to local db messages table
        databaseHelper.addMessage(newMessage);

        // Add or update the conversations table with the most recent
        // timestamp for the current Artifact.
        databaseHelper.addToOrUpdateConversations(newMessage);
        chatMessageList.add(newMessage);

        // Reset the text view to empty
        editText.setText("");

        // Send message to server and receive reply.
        new MessageAsyncTask().execute(new MessageContainer(message, artifact.getUUID()));
    }

    /**
     * Called when loading a conversation.
     */
    private void loadChatListFromDB(){
        chatMessageList = databaseHelper.returnAllMessages(artifact.getUUID());
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
                newMessage.setText(reply);
                newMessage.setFromUser(false);
                newMessage.setTimestamp(TimeStampHelper.getTimeStamp());
                newMessage.setArtifactUUID(artifact.getUUID());

                databaseHelper.addMessage(newMessage);
                chatMessageList.add(newMessage);

                // Update list of items displayed.
                adapter.notifyDataSetChanged();

                // TODO: Scroll to bottom of messages.
            } else {
                // No response received - i.e. no connection to server or error with response
                Snackbar.make(findViewById(R.id.chat_layout), "You have lost connection",
                        Snackbar.LENGTH_SHORT).show();
            }
            super.onPostExecute(reply);
        }
    }

}

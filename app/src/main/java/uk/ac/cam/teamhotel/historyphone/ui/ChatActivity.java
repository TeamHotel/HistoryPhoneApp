package uk.ac.cam.teamhotel.historyphone.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.database.ChatMessage;
import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;
import uk.ac.cam.teamhotel.historyphone.server.MessageSender;

public class ChatActivity extends AppCompatActivity {

    private static String ARTIFACT_NAME = "null";
    private DatabaseHelper dbHelper;
    private ChatAdapter adapter;

    public static List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get artifact name passed from previous fragment view
        ARTIFACT_NAME = getIntent().getStringExtra("ARTIFACT_NAME");

        //This is defined in the ChatActivity Class so only one instance is ever created (for efficiency) and so it can be accessed by the AsyncTask
        dbHelper = new DatabaseHelper(this);
        //load in messages from database
        loadChatListFromDB();

        chatMessageList.clear();
        ChatMessage exampleMessage = new ChatMessage();
        exampleMessage.setFrom_user(false);
        exampleMessage.setMessage_text("Hello, world.");
        exampleMessage.setTimestamp("sent: 11:00am");

        ChatMessage exampleMessage2 = new ChatMessage();
        exampleMessage2.setFrom_user(true);
        exampleMessage2.setMessage_text("Hello, from a user.");
        exampleMessage2.setTimestamp("sent: 11:01am");

        chatMessageList.add(exampleMessage);
        chatMessageList.add(exampleMessage2);

        ListView chatMessages = (ListView) findViewById(R.id.chat_list);
        adapter = new ChatAdapter(getApplicationContext(),chatMessageList );
        chatMessages.setAdapter(adapter);
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

        //dbHelper.addMessage(newMessage, ARTIFACT_NAME );
        chatMessageList.add(newMessage);
        //reset the text view to empty
        editText.setText("");

        //Send message to server and receive reply.
        new MessageAsyncTask().execute(message);
    }

    /**
     * Called when loading a conversation.
     */
    public void loadChatListFromDB(){
        //TODO: implement - load messages from DB into ArrayList.
        chatMessageList = dbHelper.returnAllMessages();

    }

    private class MessageAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //invoke static method to download artifact with uuid 123.
            return MessageSender.sendMessage(params[0]);
        }

        @Override
        protected void onPostExecute(String reply) {
            if (reply != null) {
                ChatMessage newMessage = new ChatMessage();
                newMessage.setMessage_text(reply);
                newMessage.setFrom_user(false);

                //dbHelper.addMessage(newMessage, ARTIFACT_NAME );
                chatMessageList.add(newMessage);
                //update list of items displayed
                adapter.notifyDataSetChanged();
            } else { //no response received - i.e. no connection to server or error with response
                //TODO: Deal with the error here
            }
            super.onPostExecute(reply);
        }
    }

}

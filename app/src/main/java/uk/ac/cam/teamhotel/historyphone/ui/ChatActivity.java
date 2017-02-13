package uk.ac.cam.teamhotel.historyphone.ui;

import android.database.sqlite.SQLiteDatabase;
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

public class ChatActivity extends AppCompatActivity {

    private static String ARTIFACT_NAME = "null";

    public static List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get artifact name passed from previous fragment view
        ARTIFACT_NAME = getIntent().getStringExtra("ARTIFACT_NAME");

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
        chatMessages.setAdapter(new ChatAdapter(getApplicationContext(),chatMessageList ));
    }

    /**
     * Called when the user taps the send button.
     */
    public void sendMessage(View view) {

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //get the text field view
        EditText editText = (EditText) findViewById(R.id.enterText);

        //setup new ChatMessage object to store in db
        ChatMessage newMessage = new ChatMessage();
        newMessage.setMessage_text(editText.getText().toString());
        newMessage.setFrom_user(true);

        dbHelper.addMessage(newMessage, ARTIFACT_NAME );
        //reset the text view to empty
        editText.setText("");


    }

    /**
     * Called when loading a conversation.
     */
    public void loadChatListFromDB(){
        //TODO: implement - load messages from DB into ArrayList.
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        chatMessageList = dbHelper.returnAllMessages();

    }

}

package uk.ac.cam.teamhotel.historyphone.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.database.ChatMessage;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    public static List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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
        // TODO: add ChatMessage object to DB.

        //get the text field view
        EditText editText = (EditText) findViewById(R.id.enterText);

        //setup new ChatMessage object to store in db
        ChatMessage newMessage = new ChatMessage();
        newMessage.setMessage_text(editText.getText().toString());
        newMessage.setFrom_user(true);

        //reset the text view to empty
        editText.setText("");

    }

    /**
     * Called when loading a conversation for the first time.
     */
    public void loadChatListFromDB(){
        //TODO: implement - load messages from DB into ArrayList.
    }

}

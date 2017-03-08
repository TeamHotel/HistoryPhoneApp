package uk.ac.cam.teamhotel.historyphone.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;

// TODO: Check workflow of timestamps / datetimes with database and the app.
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "HistoryPhoneDB";

    // Table Names:
    private static final String TABLE_ARTIFACTS = "artifacts";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_CONVERSATIONS = "conversations";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * SQL statement to create artifact table.
     */
    private static final String CREATE_ARTIFACT_TABLE =
            "CREATE TABLE IF NOT EXISTS artifacts ( " +
            "artifact_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "uuid INTEGER NOT NULL, " +
            "title TEXT NOT NULL, " +
            "description TEXT NOT NULL )";

    /**
     * SQL statement to create messages table.
     */
    private static final String CREATE_MESSAGE_TABLE =
            "CREATE TABLE IF NOT EXISTS messages ( " +
            "message_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "artifact_id INTEGER NOT NULL, " +
            "message_text TEXT NOT NULL, " +
            "from_user BOOLEAN NOT NULL, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY(artifact_id) REFERENCES artifacts(artifact_id) )";

    /**
     * SQL statement to create conversations table.
     */
    private static final String CREATE_CONVERSATIONS_TABLE =
            "CREATE TABLE IF NOT EXISTS conversations ( " +
            "uuid INTEGER PRIMARY KEY, " +
            "recent_time DATETIME )";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create artifacts table.
        db.execSQL(CREATE_ARTIFACT_TABLE);

        // Create messages table.
        db.execSQL(CREATE_MESSAGE_TABLE);

        // Create conversations table.
        db.execSQL(CREATE_CONVERSATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // On upgrade drop older tables.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIFACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS);

        // Create new tables.
        onCreate(db);
    }

    /**
     * Method to add an Artifact to the artifacts table.
     */
    public void addArtifact(Artifact artifact) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("uuid", artifact.getUUID());
        values.put("title", artifact.getName() );
        values.put("description", artifact.getDescription());

        // Insert row.
        db.insert(TABLE_ARTIFACTS, null , values);
        db.close();
    }

    public String getName(long uuid) {
        SQLiteDatabase db = getWritableDatabase();

        String name = null;

        String nameQuery = "SELECT title FROM artifacts WHERE uuid=" + uuid;
        try (Cursor cursor = db.rawQuery(nameQuery, null)) {
            // Get name if the query is successful
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(0);
            }
        }

        return name;

    }

    public String getDescription(long uuid) {
        SQLiteDatabase db = getWritableDatabase();
        String description = null;

        String descriptionQuery = "SELECT description FROM artifacts WHERE uuid=" + uuid;
        try (Cursor cursor = db.rawQuery(descriptionQuery, null)) {
            // Get name if the query is successful.
            if (cursor != null && cursor.moveToFirst()) {
                description = cursor.getString(0);
            }
        }

        return description;
    }

    /**
     * Method to add a ChatMessage to the messages table.
     */
    public void addMessage(ChatMessage chatMessage){
        SQLiteDatabase db = getWritableDatabase();

        // Get the id of the artifact that we want to add messages for.
        String getIDQuery = "SELECT artifact_id FROM artifacts WHERE uuid=" +
                chatMessage.getArtifactUUID();

        int id = -1;
        try (Cursor cursor = db.rawQuery(getIDQuery, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }
        }

        ContentValues values = new ContentValues();
        values.put("artifact_id", id);
        values.put("message_text", chatMessage.getText());
        values.put("from_user", (chatMessage.getType() == ChatMessage.TYPE_FROM_USER));
        values.put("created_at", chatMessage.getTimestamp());

        // Insert the newly built row.
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }


    /**
     * Method to add record to table if not exists else update record with latest timestamp.
     */
    public void addToOrUpdateConversations(ChatMessage chatMessage){
        SQLiteDatabase db = getWritableDatabase();

        String sqlQuery = "INSERT OR REPLACE INTO conversations (uuid, recent_time) " +
                "VALUES ("+ chatMessage.getArtifactUUID() +", '" + chatMessage.getTimestamp() + "');";
        db.execSQL(sqlQuery);
        db.close();
    }

    /**
     * Method to delete all conversation records (for recent tab population).
     */
    public void clearConversations() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM conversations");
        db.close();
    }

    /**
     * Method to delete all message records.
     */
    public void clearMessages() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM messages");
        db.close();
    }

    /**
     * Method to delete all Artifact records.
     */
    public void clearArtifacts(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM artifacts");
        db.close();
    }

    /**
     * Method to return all messages from the messages table,
     * associated with a particular uuid/Artifact. Used for
     * displaying conversations in the ChatActivity view.
     */
    public List<ChatMessage> returnAllMessages(long uuid) {
        List<ChatMessage> messageList = new ArrayList<>();

        String selectQuery = "SELECT * FROM messages, artifacts " +
                "WHERE messages.artifact_id = artifacts.artifact_id " +
                "AND artifacts.uuid=" + uuid;

        SQLiteDatabase db = getWritableDatabase();
        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            // Looping through all rows and adding to list.
            if (cursor.moveToFirst()) {
                do {
                    ChatMessage newMessage = new ChatMessage();
                    newMessage.setId(Integer.parseInt(cursor.getString(0)));
                    newMessage.setText(cursor.getString(2));
                    if (Integer.parseInt(cursor.getString(3)) == 0) {
                        newMessage.setFromUser(false);
                    } else {
                        newMessage.setFromUser(true);
                    }
                    newMessage.setTimestamp(cursor.getString(4));

                    // Adding contact to list.
                    messageList.add(newMessage);
                } while (cursor.moveToNext());
            }
        }

        return messageList;
    }

    /**
     * Method to return all conversations from the conversations table,
     * ordered by timestamp of the most recent message.
     *
     * @return a list of pairs, corresponding to the uuid and the timestamp.
     */
    public List<Pair<Long, String>> returnAllConversations() {
        List<Pair<Long, String>> entries = new ArrayList<>();

        String selectQuery = "SELECT * FROM conversations ORDER by datetime(recent_time) DESC";

        SQLiteDatabase db = getWritableDatabase();

        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            // Looping through all rows and adding to list.
            if (cursor.moveToFirst()) {
                do {
                    // Read from db row.
                    Pair<Long, String> newRow =
                            new Pair<>(Long.parseLong(cursor.getString(0)), (cursor.getString(1)));

                    // Add to list.
                    entries.add(newRow);
                } while (cursor.moveToNext());
            }
        }

        return entries;
    }

    public void printConversations() {
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlQuery = "SELECT * FROM conversations ORDER by datetime(recent_time) DESC";

        Log.d(TAG, "Conversations in database:");
        try (Cursor cursor = db.rawQuery(sqlQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Log.d(TAG, "Conversation " + cursor.getString(0) + ": " + cursor.getString(1));
                } while (cursor.moveToNext());
            }
        }
    }

    public void printMessages() {
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlQuery = "SELECT * FROM messages";

        Log.d(TAG, "Messages in database:");
        try (Cursor cursor = db.rawQuery(sqlQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Log.d(TAG, "Message " + cursor.getString(0) + ": " + cursor.getString(2));
                } while (cursor.moveToNext());
            }
        }
    }

    public void printArtifacts() {
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlQuery = "SELECT * FROM artifacts";

        Log.d(TAG, "Artifacts in database:");
        try (Cursor cursor = db.rawQuery(sqlQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Log.d(TAG, "Artifact " + cursor.getString(0) + ": " + cursor.getString(1) +
                            ", \"" + cursor.getString(2) + "\"");
                } while (cursor.moveToNext());
            }
        }
    }
}

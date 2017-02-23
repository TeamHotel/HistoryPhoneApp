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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;

// TODO: Check workflow of timestamps / datetimes with database and the app.
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "HistoryPhoneDB";

    // Table Names:
    private static final String TABLE_ARTIFACTS = "artifacts";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_CONVERSATIONS = "conversations";

    // Table IDs:
    private static final String ARTIFACTS_ID = "artifact_id";
    private static final String MESSAGES_ID = "message_id";
    private static final String MESSAGES_FOREIGN_KEY = "artifact_id";

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
            "description TEXT NOT NULL, " +
            "image BLOB )";

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
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("uuid", artifact.getUUID());
        values.put("title", artifact.getName() );
        values.put("description", artifact.getDescription());
        values.put("image", getBitmapAsByteArray(artifact.getPicture()));

        // insert row
        db.insert(TABLE_ARTIFACTS, null , values);
        db.close();

    }

    /**
     * Method to retrieve an Artifact from the artifacts table. Returns NULL object ref if unsuccessful.
     */
    public Artifact getArtifact(Long uuid){
        SQLiteDatabase db = this.getWritableDatabase();

        //default return artifact as null
        Artifact artifact = null;
        String title;
        String description;
        Bitmap picture;

        String get_artifact_query = "SELECT * FROM artifacts WHERE uuid="+uuid;
        final Cursor cursor = db.rawQuery(get_artifact_query, null);

        //construct artifact object if the query is successful
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    title = cursor.getString(2);
                    description = cursor.getString(3);
                    byte[] byteArray = cursor.getBlob(4);
                    picture = BitmapFactory.decodeByteArray(byteArray, 0,byteArray.length );

                    artifact = new Artifact(uuid, title, description, picture);

                }
            } finally {
                cursor.close();
            }
        }

        return artifact;


    }

    /**
     * Method to add a ChatMessage to the messages table.
     */
    public void addMessage(ChatMessage chatMessage){
        SQLiteDatabase db = this.getWritableDatabase();

        //get id of artifact that we want to add messages for
        String get_id_query = "SELECT artifact_id FROM artifacts WHERE uuid="+chatMessage.getUuid();
        final Cursor cursor = db.rawQuery(get_id_query, null);
        int id =0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }

        ContentValues values = new ContentValues();
        values.put("artifact_id", id );
        values.put("message_text", chatMessage.getMessage_text());
        values.put("from_user", chatMessage.isFrom_user());

        // insert row
        db.insert(TABLE_MESSAGES, null , values);
        db.close();
    }


    /**
     * Method to add record to table if not exists else update record with latest timestamp.
     */
    public void addToOrUpdateConversations(ChatMessage chatMessage){

        SQLiteDatabase db = this.getWritableDatabase();

        String sqlQuery = "INSERT OR REPLACE INTO conversations (uuid, recent_time) " +
                "VALUES ("+ chatMessage.getUuid() +", '" + chatMessage.getTimestamp() + "');";
        db.execSQL(sqlQuery);
        db.close();

    }

    //debug purposes
    public void printConversations(){
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlQuery = "SELECT * FROM conversations";
        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Log.d("CONVOS",cursor.getString(0) + " " + cursor.getString(1));

            } while (cursor.moveToNext());
        }

    }

    /**
     * Method to delete all conversation records (for recent tab population).
     */
    public void clearConversations(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM conversations");
    }


    /**
     * Method to delete all message records.
     */
    public void clearMessages(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM messages");
    }

    /**
     * Method to return all messages from the messages table, associated with a particular uuid/Artifact.
     * Used for displaying conversations in the ChatActivity view.
     */
    public List<ChatMessage> returnAllMessages(Long uuid){
        List<ChatMessage> messageList = new ArrayList<ChatMessage>();

        String selectQuery = "SELECT * FROM messages, artifacts WHERE messages.artifact_id = artifacts.artifact_id AND artifacts.uuid=" +uuid;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatMessage newMessage = new ChatMessage();
                newMessage.setMessage_id(Integer.parseInt(cursor.getString(0)));
                newMessage.setMessage_text(cursor.getString(2));
                if(Integer.parseInt(cursor.getString(3)) == 0) {
                    newMessage.setFrom_user(false);
                }else{
                    newMessage.setFrom_user(true);
                }
                newMessage.setTimestamp(cursor.getString(4));


                // Adding contact to list
                messageList.add(newMessage);

            } while (cursor.moveToNext());
        }

        return messageList;
    }

    /**
     * Method to return all conversations from the conversations table, ordered by timestamp of the most recent message.
     * This returns a list of pairs, corresponding to the uuid and the timestamp.
     */
    public List<Pair<Long, String>> returnAllConversations(){

        List<Pair<Long, String>> entries = new ArrayList<Pair<Long, String>>();
        String selectQuery = "SELECT *  FROM conversations ORDER by datetime(recent_time) DESC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //read from db row
                Pair<Long, String> newRow = new Pair<>(Long.parseLong(cursor.getString(0)), (cursor.getString(1)));

                //add to list
                entries.add(newRow);

            } while (cursor.moveToNext());
        }

        return entries;
    }

    /**
     * Method for converting bitmap to byte array for storing in the database.
     */
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}

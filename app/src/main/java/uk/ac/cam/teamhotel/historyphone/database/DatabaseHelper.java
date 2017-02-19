package uk.ac.cam.teamhotel.historyphone.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;

public class DatabaseHelper extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "HistoryPhoneDB";
    //Table Names
    private static final String TABLE_ARTIFACTS = "artifacts";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_CONVERSATIONS = "conversations";


    //Table IDs
    private static final String ARTIFACTS_ID = "artifact_id";
    private static final String MESSAGES_ID = "message_id";
    private static final String MESSAGES_FOREIGN_KEY = "artifact_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // SQL statement to create artifact table
    private static final String CREATE_ARTIFACT_TABLE = "CREATE TABLE IF NOT EXISTS artifacts ( " +
            "artifact_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "uuid INTEGER NOT NULL, " +
            "title TEXT NOT NULL, "+
            "description TEXT NOT NULL, " +
            "image BLOB NOT NULL )";

     // SQL statement to create messages table
     private static final String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS messages ( " +
            "message_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
             "artifact_id INTEGER NOT NULL, " +
            "message_text TEXT NOT NULL, "+
            "from_user BOOLEAN NOT NULL, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
             "FOREIGN KEY(artifact_id) REFERENCES artifacts(artifact_id) )";

    // SQL statement to create conversations table
    private static final String CREATE_CONVERSATIONS_TABLE = "CREATE TABLE IF NOT EXISTS conversations ( " +
            "uuid INTEGER PRIMARY KEY, " +
            "recent_time DATETIME )";

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create artifacts table
        db.execSQL(CREATE_ARTIFACT_TABLE);

        // create messages table
        db.execSQL(CREATE_MESSAGE_TABLE);

        // create conversations table
        db.execSQL(CREATE_CONVERSATIONS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIFACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS);

        // create new tables
        onCreate(db);
    }

    /*
     * Creating an Artifact
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

    public void addMessage(ChatMessage chatMessage, String artifact_name){
        SQLiteDatabase db = this.getWritableDatabase();

        //get id of artifact that we want to add messages for
        String get_id_query = "SELECT artifact_id FROM artifacts WHERE title="+artifact_name;
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

    public List<ChatMessage> returnAllMessages(){
        List<ChatMessage> messageList = new ArrayList<ChatMessage>();

        String selectQuery = "SELECT * FROM messages";

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

    //method for converting bitmap to byte array for storing in db
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}

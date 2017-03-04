package uk.ac.cam.teamhotel.historyphone.database;

public class ChatMessage {

    public static final int TYPE_FROM_ARTIFACT = 0;
    public static final int TYPE_FROM_USER = 1;

    private int id;
    private String text;
    private int type;
    private String timestamp;
    /** Associated artifactUUID, for easier processing. */
    private long artifactUUID;

    // Getters and setters.
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getType() { return type; }
    public void setFromUser(boolean fromUser) {
        type = fromUser ? TYPE_FROM_USER : TYPE_FROM_ARTIFACT;
    }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public long getArtifactUUID() { return artifactUUID; }
    public void setArtifactUUID(long artifactUUID) { this.artifactUUID = artifactUUID; }
}

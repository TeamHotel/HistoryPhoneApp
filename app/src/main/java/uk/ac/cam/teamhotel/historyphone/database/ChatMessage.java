package uk.ac.cam.teamhotel.historyphone.database;

public class ChatMessage {

    private Integer message_id;
    private String message_text;
    private boolean from_user;
    private String timestamp;

    //getters and setters
    public Integer getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Integer message_id) {
        this.message_id = message_id;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public int isFrom_user() {
        if(from_user == true){
            return 1;
        }else{
            return 0;
        }
    }

    public void setFrom_user(boolean from_user) {
        this.from_user = from_user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

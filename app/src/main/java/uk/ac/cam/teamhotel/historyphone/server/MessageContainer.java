package uk.ac.cam.teamhotel.historyphone.server;

/**
 * This class contains information to be passed to AsyncTask i.e. Message and UUID
 */

public class MessageContainer {
    private String message;
    private long uuid;

    public MessageContainer(String message, long uuid) {
        this.message = message;
        this.uuid = uuid;
    }

    public String getMessage() {
        return message;
    }

    public long getUuid() {
        return uuid;
    }
}

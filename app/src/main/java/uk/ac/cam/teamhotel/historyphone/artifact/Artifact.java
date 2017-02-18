package uk.ac.cam.teamhotel.historyphone.artifact;

import android.graphics.Bitmap;

public class Artifact {

    private long uuid;
    private String name;
    private String description;
    private Bitmap picture;

    public Artifact(long uuid, String name, String description, Bitmap picture) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    public long getUUID() { return uuid; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Bitmap getPicture() { return picture; }

    @Override
    public String toString() {
        return "Artifact(" + uuid + ", " + name + ")";
    }
}

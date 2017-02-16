package uk.ac.cam.teamhotel.historyphone.artifact;

import android.graphics.Bitmap;

public class Artifact {

    private String name;
    private String description;
    private Bitmap picture;
    private long uuid;

    public Artifact(String name, String description, Bitmap picture, long uuid){
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.uuid = uuid;
    }

    public long getUUID() {return uuid;}
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Bitmap getPicture() { return picture; }
}

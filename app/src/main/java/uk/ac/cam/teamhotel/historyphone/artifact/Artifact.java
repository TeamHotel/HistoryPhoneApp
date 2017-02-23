package uk.ac.cam.teamhotel.historyphone.artifact;

import android.graphics.Bitmap;
import android.util.Log;

public class Artifact {
    private static final String TAG = "Artifact";

    public static final Artifact LOADING = new Artifact(-1, "", "", null);

    private long uuid;
    private String name;
    private String description;
    private Bitmap picture;

    public Artifact(long uuid, String name, String description, Bitmap picture) {
        this.uuid = uuid;
        Log.d(TAG, "Artifact: Created artifact with uuid :" + uuid);
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    public long getUUID() { return uuid; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Bitmap getPicture() { return picture; }

    public void setName(String name){this.name = name;}
    public void setDescription(String desc){this.description = desc;}
    public void setPicture(Bitmap pic){this.picture = pic;}

    @Override
    public String toString() {
        return "Artifact(" + uuid + ", " + name + ")";
    }
}

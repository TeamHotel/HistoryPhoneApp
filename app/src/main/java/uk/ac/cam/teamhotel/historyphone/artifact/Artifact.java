package uk.ac.cam.teamhotel.historyphone.artifact;

import android.graphics.Bitmap;

public class Artifact {

    private String name;
    private String description;
    private Bitmap picture;

    public Artifact(String name, String description, Bitmap picture){
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getPicture() {
        return picture;
    }
}

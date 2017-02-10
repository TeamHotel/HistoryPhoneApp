package uk.ac.cam.teamhotel.historyphone;

import android.graphics.Bitmap;

import java.util.Comparator;

//To store info about the Artifacts, so we can display meta-data and cache database data easily
public class Artifact {

    String name;
    String description;
    float distance;
    Bitmap picture;

    public Artifact(String name, String description, float distance, Bitmap picture){
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.picture = picture;
    }

    //to be able to sort objects by distance for Nearby Fragment
    public int compareTo(Artifact o){
        return (int)(distance - o.distance);
    }

    //static definition of comparator, to call it nicely when sorting a collection
    public static Comparator<Artifact> SORT_BY_DIST = new Comparator<Artifact>() {
        @Override
        public int compare(Artifact o1, Artifact o2) {
            return o1.compareTo(o2);
        }
    };
}

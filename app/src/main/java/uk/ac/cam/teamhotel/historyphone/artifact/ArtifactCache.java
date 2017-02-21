package uk.ac.cam.teamhotel.historyphone.artifact;

import android.util.LongSparseArray;

/**
 * Simple cache of artifact objects, indexed by beacon UUID.
 *
 * TODO: Make thread safe.
 */
public class ArtifactCache {

    //singleton instance variable
    private static ArtifactCache instance = null;

    private LongSparseArray<Artifact> cache;

    //private constructor for singleton implementation
    private ArtifactCache() {
        cache = new LongSparseArray<>();
        // TODO: Remove once artifact loading from server works.
        cache.put(56L, new Artifact(56L, "Thing", "Some friccin type of thingo", null));
    }

    //use this method to get a single instance of the class
    public static ArtifactCache getInstance(){
        if(instance ==null){
            instance = new ArtifactCache();
        }
        return instance;
    }

    public Artifact get(long uuid) {
        return cache.get(uuid, null);
    }

    public void set(long uuid, Artifact artifact) {
        cache.put(uuid, artifact);
    }
}

package uk.ac.cam.teamhotel.historyphone.artifact;

import android.graphics.BitmapFactory;
import android.util.LongSparseArray;

import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;

/**
 * Simple cache of artifact objects, indexed by beacon UUID.
 *
 * TODO: Make thread safe.
 */
public class ArtifactCache {

    private static ArtifactCache instance;

    public static ArtifactCache getInstance() {
        if(instance == null){
            instance = new ArtifactCache();
        }
        return instance;
    }

    private LongSparseArray<Artifact> cache;

    private ArtifactCache() {
        cache = new LongSparseArray<>();
        // TODO: Remove once artifact loading from server works.
        Artifact test1 = new Artifact(0L, "Thing", "Some friccin type of thingo", null);
        Artifact test2 = new Artifact(123L, "Thing2", "test 2", null);
        cache.put(0L, test1 );
        cache.put(123L, test2);

        cache.put(56L, new Artifact(56L, "Thing", "Some friccin type of thingo", null));
    }

    public Artifact get(long uuid) {
        return cache.get(uuid, null);
    }

    public void set(long uuid, Artifact artifact) {
        cache.put(uuid, artifact);
    }
}

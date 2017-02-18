package uk.ac.cam.teamhotel.historyphone.artifact;

import android.util.LongSparseArray;

/**
 * Simple cache of artifact objects, indexed by beacon UUID.
 *
 * TODO: Make thread safe.
 */
public class ArtifactCache {

    private LongSparseArray<Artifact> cache;

    public ArtifactCache() {
        cache = new LongSparseArray<>();
        // TODO: Remove once artifact loading from server works.
        cache.put(0L, new Artifact(0L, "Thing", "Some friccin type of thingo", null));
    }

    public Artifact get(long uuid) {
        return cache.get(uuid, null);
    }

    public void set(long uuid, Artifact artifact) {
        cache.put(uuid, artifact);
    }
}

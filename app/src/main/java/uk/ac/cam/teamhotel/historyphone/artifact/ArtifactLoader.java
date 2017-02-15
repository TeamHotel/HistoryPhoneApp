package uk.ac.cam.teamhotel.historyphone.artifact;

import java.util.ArrayList;

import io.reactivex.Observable;
import uk.ac.cam.teamhotel.historyphone.server.MetaQuery;

public class ArtifactLoader {

    private ArtifactCache artifactCache;

    /**
     * Initialise the internal artifact cache.
     */
    public ArtifactLoader(/* TODO: Pass db connection. */) {
        artifactCache = new ArtifactCache();
    }

    /**
     * Load a single Artifact object from the cache. If the Artifact is not present
     * in the cache, it is loaded from the database. If the Artifact is not present
     * in the database, null is returned and a request made to the server.
     * @param uuid UUID of the Artifact object.
     * @return a reference to the loaded artifact object, or null if not found.
     */
    public Artifact load(Long uuid) {
        Artifact artifact = artifactCache.get(uuid);
        // If the artifact has not been cached, attempt to load it from the db.
        if (artifact == null) {
            // TODO: Request artifact from local database.
        }
        //retrieve artifact from server (may need AsyncTask)
        artifact = MetaQuery.getArtifact(uuid);
        return artifact;
    }

    /**
     * Return a stream to track incoming UUID arrays and convert them to Artifact
     * arrays, making use of a cache (the same UUID will always return the same,
     * immutable artifact object).
     *
     * @param uuidsStream Rx observable stream of UUID arrays.
     */
    public Observable<ArrayList<Artifact>> loadScanStream(Observable<ArrayList<Long>> uuidsStream) {
        return uuidsStream
                .map(uuids -> {
                    // Map a list of beacon UUIDs to a corresponding list of artifacts.
                    ArrayList<Artifact> artifacts = new ArrayList<>();
                    for (int i = 0; i < uuids.size(); i++) {
                        artifacts.add(load(uuids.get(i)));
                    }
                    return artifacts;
                });
    }
}

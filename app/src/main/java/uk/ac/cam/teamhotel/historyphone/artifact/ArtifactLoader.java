package uk.ac.cam.teamhotel.historyphone.artifact;

import android.os.AsyncTask;

import java.util.ArrayList;

import io.reactivex.Observable;
import uk.ac.cam.teamhotel.historyphone.server.MetadataQuery;

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
     *
     * @param uuid UUID of the requested Artifact object.
     * @return a reference to the loaded artifact object, or null if not found.
     */
    public Artifact load(Long uuid) {
        Artifact artifact = artifactCache.get(uuid);
        // If the Artifact object has not been cached, attempt to construct it from the db.
        if (artifact == null) {
            // TODO: Request artifact from local database.
        }
        // If the artifact metadata is not present in the db, concurrently retrieve it
        // from the server and return null.
        if (artifact == null) {
            retrieve(uuid);
        }
        return artifact;
    }

    /**
     * Download metadata for an artifact from the server, creating and caching an
     * object for it and storing the metadata in the artifacts table. The request
     * is performed using an AsyncTask, so operates concurrently with the UI thread.
     *
     * @param uuid UUID of the requested artifact.
     */
    public void retrieve(Long uuid) {
        new ArtifactRetrievalTask().execute(uuid);
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

    private class ArtifactRetrievalTask extends AsyncTask<Long, Void, Artifact> {

        private long uuid;

        @Override
        protected Artifact doInBackground(Long... params) {
            // Invoke static method to download artifact with uuid 123.
            uuid = params[0];
            return MetadataQuery.getArtifact(params[0]);
        }

        @Override
        protected void onPostExecute(Artifact artifact) {
            // Store the newly retrieved artifact in the cache.
            artifactCache.set(uuid, artifact);

            super.onPostExecute(artifact);
        }
    }
}

package uk.ac.cam.teamhotel.historyphone.artifact;

import android.os.AsyncTask;

import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;
import uk.ac.cam.teamhotel.historyphone.server.MetadataQuery;

public class ArtifactLoader {

    private DatabaseHelper databaseHelper;
    private ArtifactCache artifactCache;

    public ArtifactLoader(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        artifactCache = new ArtifactCache();
    }

    /**
     * Load a single Artifact object from the cache. If the Artifact is not present
     * in the cache, it is loaded from the database. If the Artifact is not present
     * in the database, a placeholder is returned and a request made to the server.
     *
     * @param uuid UUID of the requested Artifact object.
     * @return a reference to the loaded artifact object, or a placeholder if not found.
     */
    public Artifact load(Long uuid) {
        Artifact artifact = artifactCache.get(uuid);

        // If the Artifact object has not been cached, attempt to construct it from the db.
        if (artifact == null) {

            // Attempt to get Artifact from local database
            artifact = databaseHelper.getArtifact(uuid);

        }
        // If the artifact metadata is not present in the db, concurrently retrieve it
        // from the server and return the loading placeholder artifact.
        if (artifact == null) {
            retrieve(uuid);
            //return Artifact.LOADING;
            return new Artifact(-1, uuid);
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
    public void retrieve(Long uuid) { new ArtifactRetrievalTask().execute(uuid); }

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

            // Store the newly retrieved artifact in the database.
            databaseHelper.addArtifact(artifact);

            //TODO: Remove placeholder Artifact, refresh view to show when Artifact has loaded.

            super.onPostExecute(artifact);
        }
    }
}

package uk.ac.cam.teamhotel.historyphone.artifact;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;
import uk.ac.cam.teamhotel.historyphone.server.MetadataQuery;
import uk.ac.cam.teamhotel.historyphone.utils.StoreBitmapUtility;

public class ArtifactLoader {

    public static final String TAG = "ArtifactLoader";

    private DatabaseHelper databaseHelper;
    private ArtifactCache artifactCache;
    private Context context;

    public ArtifactLoader(DatabaseHelper databaseHelper, Context context) {
        this.databaseHelper = databaseHelper;
        artifactCache = new ArtifactCache();
        this.context = context;
    }

    /**
     * Load a single Artifact object from the cache. If the Artifact is not present
     * in the cache, it is loaded from the database. If the Artifact is not present
     * in the database, a placeholder is returned and a request made to the server.
     *
     * @param uuid UUID of the requested Artifact object.
     * @return a reference to the loaded artifact object, or a placeholder if not found.
     */
    public Artifact load(long uuid) {
        // Attempt to retrieve the Artifact object from the cache.
        Artifact artifact = artifactCache.get(uuid);

        // If the Artifact object has not been cached, attempt to construct it from the db.
        if (artifact == null) {
            // Attempt to load the Artifact from local database and storage.
            String name = databaseHelper.getName(uuid);
            String description = databaseHelper.getDescription(uuid);
            Bitmap picture = StoreBitmapUtility.loadImageFromStorage(uuid, context);
            if (name != null && description != null && picture != null) {
                artifact = new Artifact(uuid, name, description, picture);
            }
        }
        // If the artifact metadata is not present in the db, concurrently retrieve it
        // from the server and return a loading placeholder artifact.
        if (artifact == null) {
            retrieve(uuid);
            return Artifact.newPlaceholder(uuid);
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
    public void retrieve(long uuid) { new ArtifactRetrievalTask().execute(uuid); }

    private class ArtifactRetrievalTask extends AsyncTask<Long, Void, Artifact> {

        private long uuid;

        @Override
        protected Artifact doInBackground(Long... params) {
            // Invoke static method to download artifact with given uuid.
            uuid = params[0];
            return MetadataQuery.getArtifact(params[0]);
        }

        @Override
        protected void onPostExecute(Artifact artifact) {
            if (artifact == null) {
                Log.w(TAG, "Failed to load artifact " + uuid + " from the server.");
                return;
            }

            Log.d(TAG, "Loaded artifact " + uuid + " from the server.");

            // Store the newly retrieved artifact in the cache.
            artifactCache.set(uuid, artifact);

            // Store the newly retrieved artifact in the database.
            databaseHelper.addArtifact(artifact);
            StoreBitmapUtility.saveToInternalStorage(uuid, artifact.getPicture(), context);

            super.onPostExecute(artifact);
        }
    }
}

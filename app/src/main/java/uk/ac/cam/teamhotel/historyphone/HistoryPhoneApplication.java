package uk.ac.cam.teamhotel.historyphone;

import android.app.Application;

import uk.ac.cam.teamhotel.historyphone.artifact.ArtifactLoader;
import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;

public class HistoryPhoneApplication extends Application {

    private DatabaseHelper databaseHelper;
    private ArtifactLoader artifactLoader;

    @Override
    public void onCreate() {
        // Create the database helper.
        databaseHelper = new DatabaseHelper(this);

        // Create the artifact loader.
        artifactLoader = new ArtifactLoader(databaseHelper);
    }

    /**
     * @return a reference to the application database helper instance.
     */
    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    /**
     * @return a reference to the application artifact loader instance.
     */
    public ArtifactLoader getArtifactLoader() {
        return artifactLoader;
    }
}

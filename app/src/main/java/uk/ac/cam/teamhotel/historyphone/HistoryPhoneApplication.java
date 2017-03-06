package uk.ac.cam.teamhotel.historyphone;

import android.app.Application;
import android.content.SharedPreferences;

import uk.ac.cam.teamhotel.historyphone.artifact.ArtifactLoader;
import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;

public class HistoryPhoneApplication extends Application {

    private static final String PREFS = "PREFS_HISTORY_PHONE";
    private static final String PREF_HOSTNAME = "PREF_HOSTNAME";

    private DatabaseHelper databaseHelper;
    private ArtifactLoader artifactLoader;
    private String host;

    @Override
    public void onCreate() {
        // Create the database helper.
        databaseHelper = new DatabaseHelper(this);

        // Create the artifact loader.
        artifactLoader = new ArtifactLoader(databaseHelper, this);

        // Load the hostname.
        SharedPreferences prefs = getSharedPreferences(PREFS, 0);
        host = prefs.getString(PREF_HOSTNAME, "localhost:12345");

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

    /**
     * Set the hostname of the chatbot server.
     */
    public void setHost(String host) {
        if (!host.equals("")) {
            this.host = host;
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, 0).edit();
            editor.putString(PREF_HOSTNAME, host);
            editor.apply();
        }
    }

    /**
     * @return the hostname of the chatbot server.
     */
    public String getHost() {
        return host;
    }
}

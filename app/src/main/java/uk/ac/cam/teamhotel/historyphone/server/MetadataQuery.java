package uk.ac.cam.teamhotel.historyphone.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.utils.StoreBitmapUtility;

/**
 * This class has the ability to request images and metadata from the
 * server based on the artifact UUID.
 */
public class MetadataQuery {

    private static final String TAG = "MetadataQuery";
    private static final String INFO_URL = "http://%s/api/info?id=%d";
    private static final String IMG_URL = "http://%s/img/%d.png";

    /**
     * Get the bitmap associated with a particular artifact from the server.
     *
     * @param uuid UUID of the requested artifact.
     * @param host Hostname of the chatbot server.
     * @return the bitmap associated with an artifact.
     */
    public static Bitmap getImage(long uuid, String host) {
        Bitmap result = null;
        Log.d(TAG, "Requesting image for artifact '" + String.valueOf(uuid) + "'...");
        try {
            // Create url string.
            String urlString = String.format(Locale.UK, IMG_URL, host, uuid);

            // Open the connection connection.
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Create the input stream from HTTP connection.
            BufferedInputStream is = new BufferedInputStream(connection.getInputStream(), 8192);
            // Create byte output stream and populate it from the input stream.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte bytes[] = new byte[8192];
            int count;
            while ((count = is.read(bytes)) != -1) {
                out.write(bytes, 0, count);
            }

            // Use the byte output stream to create a bitmap.
            result = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
            Log.d(TAG, "Retrieved image for artifact '" +
                    String.valueOf(uuid) + "' successfully.");
        } catch (IOException e) {
            Log.e(TAG, "I/O error retrieving image for artifact '" +
                    String.valueOf(uuid) + "': " + e.getMessage());
        }
        return result;
    }

    /**
     * Request artifact metadata from the server and use it to build an artifact
     * object. In the event of an error, null is returned.
     *
     * @param uuid UUID of the requested artifact.
     * @param host Hostname of the chatbot server.
     * @return the newly built artifact object.
     */
    public static Artifact getArtifact(long uuid, String host) {
        Log.d(TAG, "Querying server for artifact '" + String.valueOf(uuid) + "'...");
        Artifact result = null;
        String urlString = String.format(Locale.UK, INFO_URL, host, uuid);
        try {
            // Read JSON from server.
            JSONObject jsonObject = readJsonFromUrl(urlString);

            // Extract data from JSON.
            String name = jsonObject.getString("name");
            String description = jsonObject.getString("description");
            Bitmap image = getImage(uuid, host);

            // Create new artifact
            result = new Artifact(uuid, name, description, image);
            Log.d(TAG, "Retrieved metadata for '" + String.valueOf(uuid) + "' successfully.");
        } catch (IOException | JSONException e) {
            Log.e(TAG, "I/O error retrieving metadata for artifact '" +
                    String.valueOf(uuid) + "': " + e.getMessage());
        }
        return result;
    }

    /**
     * Build a JSON object from the response of an HTTP GET request.
     *
     * @param urlString String URL of the request.
     * @return the newly built JSON object.
     */
    public static JSONObject readJsonFromUrl(String urlString) throws IOException, JSONException {
        URLConnection connection = new URL(urlString).openConnection();
        // Set timeout so the app doesn't stall.
        connection.setConnectTimeout(500);
        try (InputStream is = connection.getInputStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            // Build JSON string from input stream.
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            String jsonText = sb.toString();

            // Build JSON Object from string.
            return new JSONObject(jsonText);
        }
    }
}

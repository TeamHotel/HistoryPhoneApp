package uk.ac.cam.teamhotel.historyphone.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;

/**
 * This class has the ability to request images and metadata from the
 * server based on the artifact UUID.
 */
public class MetaQuery {

    private static final String TAG = "MetaQuery";

    /**
     * Get the bitmap associated with a particular artifact from the server.
     *
     * @param uuid UUID of the requested artifact.
     * @return the bitmap associated with an artifact.
     */
    public static Bitmap getImage(long uuid) {
        Bitmap result = null;
        Log.d(TAG, "getImage: Try to get image!");
        try {
            // Create url string.
            String urlString = "http://10.0.2.2:12345/img/" + String.valueOf(uuid) + ".png";

            // Open the connection connection.
            URL url = new URL(urlString);  // TODO: Insert the appropriate server url.
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
            Log.d(TAG, "getImage: Got the image!");
        } catch (MalformedURLException e) {
            System.err.print("There was a problem with generating the URL.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.print("There has been an I/O error.");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Request artifact metadata from the server and use
     * it to build an artifact object.
     *
     * @param uuid UUID of the requested artifact.
     * @return the newly built artifact object.
     */
    public static Artifact getArtifact(long uuid) {
        Artifact result = null;
        // TODO: Change URL for when the server is not running on localhost.
        String url = "http://10.0.2.2:12345/api/info?id=" + String.valueOf(uuid);
        try {
            //read JSON from server
            JSONObject jsonObject = readJsonFromUrl(url);

            //extract data from JSON
            String name = jsonObject.getString("name");
            String description = jsonObject.getString("description");
            Bitmap image = getImage(uuid);

            //create new artifact
            result = new Artifact(name, description, image);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Build a JSON object from the response of an HTTP GET request.
     *
     * @param url String URL of the request.
     * @return the newly built JSON object.
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8")));

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

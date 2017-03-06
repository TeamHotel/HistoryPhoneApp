package uk.ac.cam.teamhotel.historyphone.server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * MessageSender provides a class to send messages to a server and receive the reply
 */

public class MessageSender {

    /**
     * @return a string which encodes the answer message from the server.
     */
    public static String sendMessage(String message, long uuid, String host) {
        String urlString = "http://%s/api/response?message=%s&uuid=%d";

        if (message.equals("")) {
            return null;
        }

        try {
            // Encode the message in UTF-8.
            String urlMessage = URLEncoder.encode(message, "UTF-8");
            urlString = String.format(urlString, host, urlMessage, uuid);

            // Get response from server.
            JSONObject jsonObject = MetadataQuery.readJsonFromUrl(urlString);

            // Extract and return data from JSON.
            return jsonObject.getString("message");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

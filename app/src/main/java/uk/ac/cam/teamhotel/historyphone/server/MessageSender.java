package uk.ac.cam.teamhotel.historyphone.server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Message Sender provides a class to send messages to a server and receive the reply
 */

public class MessageSender {

    //returns a string which encodes the answer message from the server
    public static String sendMessage(String message) {
        String urlString = "http://10.0.2.2:12345/api/response?message=";
        try {
            //encode the message properly
            String urlMessage = URLEncoder.encode(message, "UTF-8");
            urlString += urlMessage;

            //Get response from server
            JSONObject jsonObject = MetaQuery.readJsonFromUrl(urlString);

            //extract and return data from JSON
            return jsonObject.getString("message");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (JSONException j) {
            j.printStackTrace();
        }
        return null;
    }
}

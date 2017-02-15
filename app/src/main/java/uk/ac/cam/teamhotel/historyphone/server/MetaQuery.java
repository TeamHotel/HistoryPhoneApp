package uk.ac.cam.teamhotel.historyphone.server;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;


public class MetaQuery {
    private static final String TAG = "MetaQuery";

    public static Bitmap getImage(long uuid) {
        Bitmap result = null;
        Log.d(TAG, "getImage: Try to get image!");
        try {
            //create connection
            URL url = new URL("http://10.0.2.2:12345/img/RPiLogo.png");//TODO: Insert the appropriate server url
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            //create the input stream from http connection
            BufferedInputStream is = new BufferedInputStream(connection.getInputStream(), 8192);
            //create byte output stream and populate it from the input stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte bytes[] = new byte[8192];
            int count;
            while ((count = is.read(bytes)) != -1) {
                out.write(bytes, 0, count);
            }
            //use the byte output stream to create a bitmap
            result = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
            Log.d(TAG, "getImage: Got the image!");

        } catch (MalformedURLException m) {
            System.err.print("There was a problem with generating the URL");
        } catch (IOException e) {
            System.err.print("There has been an I/O error");
            e.printStackTrace();
        }
        return result;
    }
}

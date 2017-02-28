package uk.ac.cam.teamhotel.historyphone.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class DbBitmapUtility {

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        if(stream.toByteArray() == null) {
            Log.d("GET BYTES FUNC", "NULL BYTE ARRAY");
        }
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {

        Bitmap result = BitmapFactory.decodeByteArray(image, 0, image.length);
        if(result ==null) {
            Log.d("BITMAP CONVERT FAIL", "failed to convert to bitmap");
        }
        return result;
    }
}
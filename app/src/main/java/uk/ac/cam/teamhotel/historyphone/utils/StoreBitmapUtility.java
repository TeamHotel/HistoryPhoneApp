package uk.ac.cam.teamhotel.historyphone.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StoreBitmapUtility {

    public static String saveToInternalStorage(long uuid, Bitmap bitmapImage, Context context) {
        // Path to "/data/data/HistoryPhoneApp/app_data/imageDir"
        File directory = context.getDir("imageDir", Context.MODE_PRIVATE);

        // Create imageDir.
        File myPath = new File(directory, uuid + ".png");

        try (FileOutputStream fos = new FileOutputStream(myPath)) {
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return directory.getAbsolutePath();
    }

    public static Bitmap loadImageFromStorage(long uuid, Context context) {
        try {
            File directory = context.getDir("imageDir", Context.MODE_PRIVATE);
            File file = new File(directory, uuid + ".png");
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return null;
        }

    }

}

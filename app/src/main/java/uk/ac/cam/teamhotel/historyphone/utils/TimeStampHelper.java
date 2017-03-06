package uk.ac.cam.teamhotel.historyphone.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeStampHelper {

    public static String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sDF.format(date);
    }

    public static String formatTimeStamp(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            Date date = sdf.parse(timestamp);

            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            return sdf2.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static String formatTime(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            Date date = sdf.parse(timestamp);

            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

            return sdf2.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "error";
    }
}

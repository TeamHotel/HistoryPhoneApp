package uk.ac.cam.teamhotel.historyphone.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStampHelper {

    public static String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat sDF = new SimpleDateFormat("h:mm a");
        return "Sent: " + sDF.format(date);
    }
}

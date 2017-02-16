package uk.ac.cam.teamhotel.historyphone.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by charl on 15/02/2017.
 */

public class TimeStampHelper {

    public static String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat sDF = new SimpleDateFormat("h:mm a"); //edit this for different timestamps
        //https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        return "Sent: " + sDF.format(date);
    }
}

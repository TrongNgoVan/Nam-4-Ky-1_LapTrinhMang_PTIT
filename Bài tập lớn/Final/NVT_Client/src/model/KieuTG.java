package model;

import model.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 *
 * @author Ngọ Văn Trọng
 */
public class KieuTG {
    public static String getCurrentTimeFormatted() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static String secondsToMinutes(int seconds) {
        return addZero(seconds / 60) + ":" + addZero(seconds % 60);
    }

    public static String addZero(int n) {
        if (n < 10) {
            return "0" + n;
        }
        return "" + n;
    }
}

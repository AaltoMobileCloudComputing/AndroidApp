package com.group16.mcc;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    private static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:ss");
    private static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:ss");

    public static String formatDate(Date date) {
        return sdfDate.format(date);
    }

    public static String formatTime(Date date) {
        return sdfTime.format(date);
    }

    public static String formatDateTime(Date date) {
        return sdfDateTime.format(date);
    }

    public static String formatDateTimeRange(Date start, Date end) {
        if (start.equals(end)) {
            return String.format("%s %s", sdfDate.format(start), sdfTime.format(start));
        } else if (start.getYear() == end.getYear() &&  // Check if same day
                start.getMonth() == end.getMonth() &&
                start.getDate() == end.getDate()){
            return String.format("%s %s - %s", sdfDate.format(start), sdfTime.format(start), sdfTime.format(end));
        } else {
            return String.format("%s %s - %s %s", sdfDate.format(start), sdfTime.format(start), sdfDate.format(end), sdfTime.format(end));
        }
    }
}

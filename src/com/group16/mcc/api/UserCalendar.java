package com.group16.mcc.api;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.TimeZone;

public class UserCalendar {

    public String name;
    public long id;

    public UserCalendar(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addEvent(Event event, ContentResolver cr) {
        // Sample from http://stackoverflow.com/questions/7859005

        // Construct event details
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(event.start.toDate());
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(event.end.toDate());
        endMillis = endTime.getTimeInMillis();

        // Insert Event
        ContentValues values = new ContentValues();
        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.TITLE, event.title);
        values.put(CalendarContract.Events.DESCRIPTION, event.description);
        values.put(CalendarContract.Events.CALENDAR_ID, id);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }

}

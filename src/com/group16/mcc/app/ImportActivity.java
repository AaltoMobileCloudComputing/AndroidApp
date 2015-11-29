package com.group16.mcc.app;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.group16.mcc.Util;
import com.group16.mcc.api.Event;
import com.group16.mcc.api.ImportEvent;
import com.group16.mcc.api.MccApi;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ImportActivity extends AppCompatActivity implements Callback<Event> {

    private List<ImportEvent> importEvents = new ArrayList<>();
    private RecyclerView.Adapter importListAdapter;
    private static final MccApi api = Util.getApi();
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_event_list);
        getImportEvents();
        token = getIntent().getStringExtra("token");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select event");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getImportEvents();
        RecyclerView importList = (RecyclerView) findViewById(R.id.import_event_list);
        importList.setHasFixedSize(true);
        RecyclerView.LayoutManager importListManager = new LinearLayoutManager(this);
        importList.setLayoutManager(importListManager);
        importListAdapter = new ImportActivityAdapter(importEvents, this);
        importList.setAdapter(importListAdapter);

    }

    public String getCalendarName(long id) {
        // Sample from http://developer.android.com/guide/topics/providers/calendar-provider.html

        // Projection array. Creating indices for this array instead of doing
        // dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars._ID + "= ?))";
        String[] selectionArgs = new String[] {String.valueOf(id)};
        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        if (cur == null)
            return null;
        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            return displayName;
        }

        return null;
    }


    public void getImportEvents() {
        // Sample from http://stackoverflow.com/questions/13232717
        Uri uri = CalendarContract.Events.CONTENT_URI;
        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events._ID,          // 0
                CalendarContract.Events.CALENDAR_ID,  // 1
                CalendarContract.Events.TITLE,        // 2
                CalendarContract.Events.DESCRIPTION,  // 3
                CalendarContract.Events.DTSTART,      // 4
                CalendarContract.Events.DTEND         // 5
        };
        String selection = "((" + CalendarContract.Calendars.OWNER_ACCOUNT + " != ?) " +
                "AND (" + CalendarContract.Calendars.OWNER_ACCOUNT + " != ?)" +
                "AND (" + CalendarContract.Events.DTSTART + " > ?))";
        //String selector = "(" + CalendarContract.Events.DTSTART + " > ?)";
        String[] selectorArguments = new String[] {
                "#contacts@group.v.calendar.google.com",
                "fi.finnish#holiday@group.v.calendar.google.com",
                String.valueOf(System.currentTimeMillis())
        };
        Cursor cursor = getContentResolver().query(uri, EVENT_PROJECTION, selection, selectorArguments, null);

        while (cursor.moveToNext()) {
            ImportEvent event = new ImportEvent();
            event.id = cursor.getLong(0);
            event.calendar_id = cursor.getLong(1);
            event.calendar_name = getCalendarName(event.calendar_id);
            event.title = cursor.getString(2);
            event.description = cursor.getString(3);
            Calendar startTime = Calendar.getInstance();
            startTime.setTimeInMillis(cursor.getLong(4));
            event.start = new DateTime(startTime);
            Calendar endTime = Calendar.getInstance();
            endTime.setTimeInMillis(cursor.getLong(5));
            event.end = new DateTime(endTime);
            importEvents.add(event);
        }
    }

    public void addEventToBackEnd(Event event) {
        Call<Event> call = api.createEvent(event, token);
        call.enqueue(this);
    }


    @Override
    public void onResponse(Response<Event> response, Retrofit retrofit) {
        super.finish();
    }

    @Override
    public void onFailure(Throwable t) {
        System.out.print("Error");
    }
}

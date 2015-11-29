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

import com.group16.mcc.api.Event;
import com.group16.mcc.api.UserCalendar;

import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    private List<UserCalendar> userCalendars = new ArrayList<>();
    private RecyclerView.Adapter shareListAdapter;
    public Event shareEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_list);

        shareEvent = getIntent().getParcelableExtra("event");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select calendar");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getCalendars();
        RecyclerView shareList = (RecyclerView) findViewById(R.id.shareList);
        shareList.setHasFixedSize(true);
        RecyclerView.LayoutManager shareListManager = new LinearLayoutManager(this);
        shareList.setLayoutManager(shareListManager);
        shareListAdapter = new ShareListAdapter(userCalendars, this);
        shareList.setAdapter(shareListAdapter);

    }

    public void getCalendars() {
        // Sample from http://developer.android.com/guide/topics/providers/calendar-provider.html

        // Projection array. Creating indices for this array instead of doing
        // dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT,                 // 3
                CalendarContract.Calendars.ACCOUNT_TYPE,                  // 4
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
        final int PROJECTION_ACCOUNT_TYPE_INDEX = 4;

        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.OWNER_ACCOUNT + " != ?) " +
                "AND (" + CalendarContract.Calendars.OWNER_ACCOUNT + " != ?))";
        String[] selectionArgs = new String[] {"#contacts@group.v.calendar.google.com",
                "fi.finnish#holiday@group.v.calendar.google.com"};
        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

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


            /*
            System.out.println(displayName);
            System.out.println(accountName);
            System.out.println(ownerName);
            */
            UserCalendar cal = new UserCalendar(calID, displayName);
            userCalendars.add(cal);


        }
    }


}

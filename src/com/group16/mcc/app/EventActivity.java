package com.group16.mcc.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.group16.mcc.Util;
import com.group16.mcc.api.Event;

public class EventActivity extends AppCompatActivity {
    private static final String TAG = "EventActivity";

    EditText title;
    EditText description;
    EditText fromDate;
    EditText fromTime;
    EditText toDate;
    EditText toTime;
    CheckBox allDay;


    Event event;
    String selectedTimePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);

        // Set toolbar and enable back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        event = getIntent().getParcelableExtra("event");

        title = (EditText) findViewById(R.id.event_title);
        description = (EditText) findViewById(R.id.event_description);
        fromDate = (EditText) findViewById(R.id.from_date);
        fromTime = (EditText) findViewById(R.id.from_time);
        toDate = (EditText) findViewById(R.id.to_date);
        toTime = (EditText) findViewById(R.id.to_time);

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog("fromDate");
            }
        });
        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog("fromTime");
            }
        });
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog("toDate");
            }
        });
        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog("toTime");
            }
        });

        if (event == null) {
            getSupportActionBar().setTitle(R.string.new_event);
            Date now = new Date();
            fromDate.setText(Util.formatDate(now));
            toDate.setText(Util.formatDate(now));
            fromTime.setText(Util.formatTime(now));
            toTime.setText(Util.formatTime(now));
        } else {
            getSupportActionBar().setTitle(R.string.edit_event);
            title.setText(event.title);
            description.setText(event.description);
            fromDate.setText(Util.formatDate(event.start));
            toDate.setText(Util.formatDate(event.end));
            fromTime.setText(Util.formatTime(event.start));
            toTime.setText(Util.formatTime(event.end));
        }
    }

    private void showDatePickerDialog(final String dialogTag) {
        DialogFragment datePickerFragment = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog d = new DatePickerDialog(getActivity(), new DatePickerChangeListener(), year, month, day);
                d.getDatePicker().setTag(dialogTag);
                return d;
            }
        };
        datePickerFragment.show(getSupportFragmentManager(), dialogTag);
    }

    private void showTimePickerDialog(final String dialogTag) {
        // There is no easy way to get TimePicker from TimePickerDialog (unlike with the date version, WTF!)
        // so we need to pass selected picker like this
        selectedTimePicker = dialogTag;
        DialogFragment timePickerFragment = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                return new TimePickerDialog(getActivity(), new TimePickerChangeListener(), hour, minute, true);
            }
        };
        timePickerFragment.show(getSupportFragmentManager(), dialogTag);
    }

    public class DatePickerChangeListener implements DatePickerDialog.OnDateSetListener {

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String pickerTag = view.getTag().toString();
            EditText textToChange;
            if ("fromDate".equals(pickerTag)) {
                textToChange = fromDate;
            } else if ("toDate".equals(pickerTag)) {
                textToChange = toDate;
            } else {
                Log.e(TAG, "Invalid fragment tag: " + pickerTag);
                return;
            }
            textToChange.setText(String.format("%04d-%02d-%02d", year, month, day));
        }
    }

    public class TimePickerChangeListener implements TimePickerDialog.OnTimeSetListener {

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String pickerTag = selectedTimePicker;
            selectedTimePicker = null;
            EditText textToChange;
            if ("fromTime".equals(pickerTag)) {
                textToChange = fromTime;
            } else if ("toTime".equals(pickerTag)) {
                textToChange = toTime;
            } else {
                Log.e(TAG, "Invalid fragment tag: " + pickerTag);
                return;
            }
            textToChange.setText(String.format("%02d:%02d", hourOfDay, minute));
        }
    }
}

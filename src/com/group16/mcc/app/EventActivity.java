package com.group16.mcc.app;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.UUID;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import com.group16.mcc.Util;
import com.group16.mcc.api.Event;
import com.group16.mcc.api.MccApi;

public class EventActivity extends AppCompatActivity implements Callback<Event> {
    private static final String TAG = "EventActivity";

    private EditText titleView;
    private EditText descriptionView;
    private EditText fromDateView;
    private EditText fromTimeView;
    private EditText toDateView;
    private EditText toTimeView;
    private CheckBox allDayCheckBox;
    private ProgressBar eventProgress;

    private static final MccApi api = Util.getApi();

    private Event activityEvent;
    private String token;
    private String selectedTimePicker;

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

        activityEvent = getIntent().getParcelableExtra("event");
        token = getIntent().getStringExtra("token");

        titleView = (EditText) findViewById(R.id.event_title);
        descriptionView = (EditText) findViewById(R.id.event_description);
        fromDateView = (EditText) findViewById(R.id.from_date);
        fromTimeView = (EditText) findViewById(R.id.from_time);
        toDateView = (EditText) findViewById(R.id.to_date);
        toTimeView = (EditText) findViewById(R.id.to_time);
        allDayCheckBox = (CheckBox) findViewById(R.id.all_day);
        allDayCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allDayClicked();
            }
        });

        fromDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog("fromDate");
            }
        });
        fromTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog("fromTime");
            }
        });
        toDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog("toDate");
            }
        });
        toTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog("toTime");
            }
        });

        FloatingActionButton newEventButton = (FloatingActionButton) findViewById(R.id.save_event_button);
        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEvent();
            }
        });

        FloatingActionButton deleteEventButton = (FloatingActionButton) findViewById(R.id.delete_event_button);
        if (activityEvent == null) {
            deleteEventButton.setVisibility(View.GONE);
        } else {
            deleteEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteEvent();
                }
            });
        }

        eventProgress = (ProgressBar) findViewById(R.id.event_progress);

        if (activityEvent == null) {
            getSupportActionBar().setTitle(R.string.new_event);
            DateTime now = new DateTime();
            fromDateView.setText(Util.formatDate(now));
            toDateView.setText(Util.formatDate(now));
            fromTimeView.setText(Util.formatTime(now));
            toTimeView.setText(Util.formatTime(now));
        } else {
            getSupportActionBar().setTitle(R.string.edit_event);
            titleView.setText(activityEvent.title);
            descriptionView.setText(activityEvent.description);
            fromDateView.setText(Util.formatDate(activityEvent.start));
            toDateView.setText(Util.formatDate(activityEvent.end));
            fromTimeView.setText(Util.formatTime(activityEvent.start));
            toTimeView.setText(Util.formatTime(activityEvent.end));
        }
    }

    private void allDayClicked() {
        if (allDayCheckBox.isChecked()) {
            fromTimeView.setVisibility(View.GONE);
            toTimeView.setVisibility(View.GONE);
        } else {
            fromTimeView.setVisibility(View.VISIBLE);
            toTimeView.setVisibility(View.VISIBLE);
        }
    }

    private void deleteEvent() {
        eventProgress.setVisibility(View.VISIBLE);
        Call<Event> call = api.deleteEvent(activityEvent._id, token);
        call.enqueue(this);
    }

    private void saveEvent() {
        View focusView = null;
        Event event;
        if (activityEvent == null) {
            event = new Event();
        } else {
            event = activityEvent;
        }

        event.title = titleView.getText().toString();
        if (TextUtils.isEmpty(event.title)) {
            titleView.setError(getString(R.string.field_required));
            focusView = titleView;
        }

        event.description = descriptionView.getText().toString();

        LocalDate fromDate = LocalDate.parse(fromDateView.getText().toString());
        LocalDate toDate = LocalDate.parse(toDateView.getText().toString());
        if (!allDayCheckBox.isChecked()) {
            LocalTime fromTime = LocalTime.parse(fromTimeView.getText().toString());
            LocalTime toTime = LocalTime.parse(toTimeView.getText().toString());
            DateTime fromDateTime = fromDate.toDateTime(fromTime);
            DateTime toDateTime = toDate.toDateTime(toTime);
            if (fromDateTime.isAfter(toDateTime)) {
                if (fromDate.isAfter(toDate)) {
                    toDateView.setError(getString(R.string.end_date_before));   // FIXME: Only shows error icon (because element not focusable)
                    focusView = toDateView;
                } else {
                    toTimeView.setError(getString(R.string.end_time_before));
                    focusView = toTimeView;
                }
            } else {
                event.start = fromDateTime;
                event.end = toDateTime;
            }
        } else if (fromDate.isAfter(toDate)) {
            toDateView.setError(getString(R.string.end_date_before));
            focusView = toDateView;
        } else {
            event.start = fromDate.toDateTime(LocalTime.MIDNIGHT);
            event.end = toDate.toDateTime(LocalTime.MIDNIGHT);
        }

        if (focusView != null) {
            focusView.requestFocus();
            return;
        }

        eventProgress.setVisibility(View.VISIBLE);
        if (activityEvent == null) {
            Call<Event> call = api.createEvent(event, token);
            call.enqueue(this);
        } else {
            Call<Event> call = api.updateEvent(event, event._id, token);
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(Response<Event> response, Retrofit retrofit) {
        eventProgress.setVisibility(View.INVISIBLE);
        if (response.isSuccess()) {
            setResult(RESULT_OK);
            super.finish();
        } else if (response.code() == 400) {
            try {
                Toast.makeText(this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e(TAG, "Error retrieving error response", e);
            }
        } else {
            Log.e(TAG, "Error when trying to create or update event");
        }
    }

    @Override
    public void onFailure(Throwable t) {
        eventProgress.setVisibility(View.INVISIBLE);
        if (t instanceof SocketTimeoutException) {
            Toast.makeText(this, "Can't connect to backend", Toast.LENGTH_LONG).show();
        }
    }

    private void showDatePickerDialog(final String dialogTag) {
        DialogFragment datePickerFragment = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                EditText dateView = getDateTimeView(dialogTag);
                LocalDate date;
                if (dateView != null) {
                    date = LocalDate.parse(dateView.getText().toString());
                } else {
                    date = new LocalDate();
                }
                int year = date.getYear();
                int month = date.getMonthOfYear()-1; // DatePickerDialog wants month in range [0,11]
                int day = date.getDayOfMonth();
                Log.d(TAG, String.format("year: %d, month: %d, day: %d", year, month ,day));

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
                EditText dateView = getDateTimeView(dialogTag);
                LocalTime time;
                if (dateView != null) {
                    time = LocalTime.parse(dateView.getText().toString());
                } else {
                    time = new LocalTime();
                }
                int hour = time.getHourOfDay();
                int minute = time.getMinuteOfHour();

                return new TimePickerDialog(getActivity(), new TimePickerChangeListener(), hour, minute, true);
            }
        };
        timePickerFragment.show(getSupportFragmentManager(), dialogTag);
    }

    public class DatePickerChangeListener implements DatePickerDialog.OnDateSetListener {

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String pickerTag = view.getTag().toString();
            EditText viewToChange = getDateTimeView(pickerTag);
            if (viewToChange != null) {
                viewToChange.setText(String.format("%04d-%02d-%02d", year, month+1, day)); // Month is in range [0,11]
                viewToChange.setError(null);
            }
        }
    }

    public class TimePickerChangeListener implements TimePickerDialog.OnTimeSetListener {

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String pickerTag = selectedTimePicker;
            selectedTimePicker = null;
            EditText viewToChange = getDateTimeView(pickerTag);
            if (viewToChange != null) {
                viewToChange.setText(String.format("%02d:%02d", hourOfDay, minute));
                viewToChange.setError(null);
            }
        }
    }

    private EditText getDateTimeView(String viewName) {
        switch (viewName) {
            case "toDate":
                return toDateView;
            case "toTime":
                return toTimeView;
            case "fromDate":
                return fromDateView;
            case "fromTime":
                return fromTimeView;
            default:
                Log.e(TAG, "Invalid date time view name: " + viewName);
                return null;
        }
    }
}

package com.group16.mcc.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.group16.mcc.Util;
import com.group16.mcc.api.Event;
import com.group16.mcc.api.MccApi;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements Callback<List<Event>> {
    private static final String TAG = "MainActivity";

    private static final int NEW_EVENT = 1;
    private static final int EDIT_EVENT = 2;

    private RecyclerView.Adapter eventListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final MccApi api = Util.getApi();

    private String token;
    private final List<Event> events = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        token = getIntent().getStringExtra("token");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton newEventButton = (FloatingActionButton) findViewById(R.id.new_event_button);
        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEvent();
            }
        });

        newEventButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openImport();
                return true;
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        RecyclerView eventList = (RecyclerView) findViewById(R.id.eventList);
        eventList.setHasFixedSize(true);
        RecyclerView.LayoutManager eventListLayoutManager = new LinearLayoutManager(this);
        eventList.setLayoutManager(eventListLayoutManager);
        eventListAdapter = new EventListAdapter(events, this);
        eventList.setAdapter(eventListAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh();
    }

    void newEvent() {
        Intent eventActivity = new Intent(MainActivity.this, EventActivity.class);
        eventActivity.putExtra("token", token);
        MainActivity.this.startActivityForResult(eventActivity, NEW_EVENT);
    }

    void editEvent(Event event, View view) {
        Intent eventActivity = new Intent(MainActivity.this, EventActivity.class);
        eventActivity.putExtra("event", event);
        eventActivity.putExtra("token", token);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation( // This is just for screen transition
                MainActivity.this, new Pair<View, String>(view.findViewById(R.id.event_card_title), getString(R.string.transition_title))
        );
        ActivityCompat.startActivityForResult(MainActivity.this, eventActivity, EDIT_EVENT, options.toBundle());
    }

    public void openShare(Event event, View view) {
        Intent shareActivity = new Intent(MainActivity.this, ShareActivity.class);
        shareActivity.putExtra("event", event);
        ActivityCompat.startActivity(MainActivity.this, shareActivity, null);
    }

    public void openImport() {
        Intent importActivity = new Intent(MainActivity.this, ImportActivity.class);
        importActivity.putExtra("token", token);
        ActivityCompat.startActivity(MainActivity.this, importActivity, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String message = null;
            switch (requestCode) {
                case NEW_EVENT:
                    message = "New event created";
                    break;
                case EDIT_EVENT:
                    message = "Changes saved";
                    break;
                default:
                    Log.e(TAG, "Unknown request code: " + requestCode);
            }
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void refresh() {
        Log.i(TAG, "Starting refresh");
        setProgressBarIndeterminateVisibility(true);
        Call<List<Event>> call = api.getEvents(token);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Response<List<Event>> response, Retrofit retrofit) {
        setProgressBarIndeterminateVisibility(false);
        events.clear();
        events.addAll(response.body());
        Log.i(TAG, String.format("Received %d event(s)", response.body().size()));
        eventListAdapter.notifyDataSetChanged();
        onItemsLoadComplete();
    }

    private void onItemsLoadComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(Throwable t) {
        Log.e(TAG, "Error when retrieving events", t);
    }
}

package com.group16.mcc.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import com.group16.mcc.api.Event;
import com.group16.mcc.api.MccApi;

public class MainActivity extends AppCompatActivity implements Callback<List<Event>> {
    private static final String TAG = "MainActivity";

    private RecyclerView eventList;
    private RecyclerView.Adapter eventListAdapter;
    private RecyclerView.LayoutManager eventListLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String BASE_URL = "http://10.0.2.2:3000/api/"; // 10.0.2.2 is host loopback address
    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

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

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        eventList = (RecyclerView) findViewById(R.id.eventList);
        eventList.setHasFixedSize(true);
        eventListLayoutManager = new LinearLayoutManager(this);
        eventList.setLayoutManager(eventListLayoutManager);
        eventListAdapter = new EventListAdapter(events, this);
        eventList.setAdapter(eventListAdapter);

        refresh();
    }

    void newEvent() {
        Intent eventActivity = new Intent(MainActivity.this, EventActivity.class);
        MainActivity.this.startActivity(eventActivity);
    }

    void editEvent(Event event, View view) {
        Intent eventActivity = new Intent(MainActivity.this, EventActivity.class);
        eventActivity.putExtra("event", event);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                // the context of the activity
                MainActivity.this,

                // For each shared element, add to this method a new Pair item,
                // which contains the reference of the view we are transitioning *from*,
                // and the value of the transitionName attribute
                new Pair<View, String>(view.findViewById(R.id.event_card_title),
                        getString(R.string.transition_title))
        );
        ActivityCompat.startActivity(MainActivity.this, eventActivity, options.toBundle());
        //MainActivity.this.startActivity(eventActivity);
    }

    private void refresh() {
        Log.i(TAG, "Starting refresh");
        setProgressBarIndeterminateVisibility(true);
        MccApi api = retrofit.create(MccApi.class);
        Call<List<Event>> call = api.getEvents(token);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Response<List<Event>> response, Retrofit retrofit) {
        setProgressBarIndeterminateVisibility(false);
        events.clear();
        for (Event event : response.body()) {
            events.add(event);
        }
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

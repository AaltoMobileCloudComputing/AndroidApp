package com.group16.mcc.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class MainActivity extends Activity implements Callback<List<Event>> {
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
                // Refresh items
                refresh();
            }
        });

        eventList = (RecyclerView) findViewById(R.id.eventList);
        eventList.setHasFixedSize(true);
        eventListLayoutManager = new LinearLayoutManager(this);
        eventList.setLayoutManager(eventListLayoutManager);
        eventListAdapter = new EventListAdapter(events);
        eventList.setAdapter(eventListAdapter);

        refresh();
    }

    private void newEvent() {
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

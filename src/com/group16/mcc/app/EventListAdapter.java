package com.group16.mcc.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group16.mcc.api.Event;
import com.group16.mcc.app.R;

/**
 * Adapted from http://www.vogella.com/tutorials/AndroidRecyclerView/article.html
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private List<Event> events;
    private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdfTime = new SimpleDateFormat("HH:ss");

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView time;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            time = (TextView) v.findViewById(R.id.time);
        }
    }

    public void add(int position, Event event) {
        events.add(position, event);
        notifyItemInserted(position);
    }

    public void remove(Event event) {
        int position = events.indexOf(event);
        events.remove(position);
        notifyItemRemoved(position);
    }

    public EventListAdapter(List<Event> events) {
        this.events = events;
    }

    @Override
    public EventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Event event = events.get(position);
        holder.title.setText(event.title);
        holder.time.setText(formatTime(event.start, event.end));

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static String formatTime(Date start, Date end) {
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
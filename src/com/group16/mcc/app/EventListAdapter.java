package com.group16.mcc.app;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group16.mcc.Util;
import com.group16.mcc.api.Event;

/**
 * Adapted from http://www.vogella.com/tutorials/AndroidRecyclerView/article.html
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private List<Event> events;

    private MainActivity main;

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public TextView title;
        public TextView time;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            title = (TextView) v.findViewById(R.id.event_card_title);
            time = (TextView) v.findViewById(R.id.event_card_time);
        }


        @Override
        public void onClick(View view) {
            main.editEvent(events.get(getAdapterPosition()), view);
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

    public EventListAdapter(List<Event> events, MainActivity main) {
        this.events = events;
        this.main = main;
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
        holder.time.setText(Util.formatDateTimeRange(event.start, event.end));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }



}
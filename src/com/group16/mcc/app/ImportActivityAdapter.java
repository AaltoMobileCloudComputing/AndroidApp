package com.group16.mcc.app;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group16.mcc.Util;
import com.group16.mcc.api.ImportEvent;

import java.util.List;

public class ImportActivityAdapter extends RecyclerView.Adapter<ImportActivityAdapter.ViewHolder> {

    private List<ImportEvent> importEvents;
    private ImportActivity main;

    public ImportActivityAdapter(List<ImportEvent> importEvents, ImportActivity importActivity) {
        this.importEvents = importEvents;
        this.main = importActivity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView calendarName;
        public TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.import_card_title);
            calendarName = (TextView) itemView.findViewById(R.id.import_card_calendar_name);
            time = (TextView) itemView.findViewById(R.id.import_card_time);
        }

        @Override
        public void onClick(View v) {
            ImportEvent importEvent = importEvents.get(getAdapterPosition());
            main.addEventToBackEnd(importEvent.getEvent());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.import_event_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ImportEvent importEvent = importEvents.get(position);
        holder.title.setText(importEvent.title);
        holder.calendarName.setText(importEvent.calendar_name);
        holder.time.setText(Util.formatDateTimeRange(importEvent.start, importEvent.end));
    }


    @Override
    public int getItemCount() {
        return importEvents.size();
    }
}

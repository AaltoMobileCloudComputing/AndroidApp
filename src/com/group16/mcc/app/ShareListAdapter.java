package com.group16.mcc.app;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group16.mcc.api.UserCalendar;

import java.util.List;

public class ShareListAdapter extends RecyclerView.Adapter<ShareListAdapter.ViewHolder> {

    private List<UserCalendar> userCalendars;
    private ShareActivity main;

    public ShareListAdapter(List<UserCalendar> userCalendars, ShareActivity mainActivity) {
        this.userCalendars = userCalendars;
        this.main = mainActivity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.calendar_title);
        }

        @Override
        public void onClick(View v) {
            userCalendars.get(getAdapterPosition()).addEvent(main.shareEvent, main.getContentResolver());
            main.finish();
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_calendar_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserCalendar userCalendar = userCalendars.get(position);
        holder.title.setText(userCalendar.name);
    }

    @Override
    public int getItemCount() {
        return userCalendars.size();
    }
}

package com.group16.mcc.api;

import org.joda.time.DateTime;

public class ImportEvent {

    public long id;
    public long calendar_id;
    public String calendar_name;
    public String title;
    public String description;
    public DateTime start;
    public DateTime end;

    public Event getEvent() {
        Event event = new Event();
        event.title = title;
        event.description = description;
        event.start = start;
        event.end = end;
        return event;
    }

}

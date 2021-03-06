package com.group16.mcc.api;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

/**
 * Generated with http://www.parcelabler.com/
 */
public class Event implements Parcelable {
    public String _id;
    public String calendar;
    public String title;
    public String description;
    public DateTime start;
    public DateTime end;

    public Event() { }

    protected Event(Parcel in) {
        _id = in.readString();
        calendar = in.readString();
        title = in.readString();
        description = in.readString();
        long tmpStart = in.readLong();
        start = tmpStart != -1 ? new DateTime(tmpStart) : null;
        long tmpEnd = in.readLong();
        end = tmpEnd != -1 ? new DateTime(tmpEnd) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(calendar);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(start != null ? start.getMillis() : -1L);
        dest.writeLong(end != null ? end.getMillis() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}

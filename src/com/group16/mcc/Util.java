package com.group16.mcc;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.group16.mcc.api.MccApi;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class Util {
    private static final String BASE_URL = "http://134.168.42.186:3000/api/"; // 10.0.2.2 is host loopback address

    private static final DateTimeFormatter fmtDate = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter fmtTime = DateTimeFormat.forPattern("HH:ss");
    private static final DateTimeFormatter fmtDateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:ss");

    public static MccApi getApi() {
        Gson gson = Converters.registerAll(new GsonBuilder()).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Util.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(MccApi.class);
    }

    public static String formatDate(DateTime dt) {
        return fmtDate.print(dt);
    }

    public static String formatTime(DateTime dt) {
        return fmtTime.print(dt);
    }

    public static String formatDateTime(DateTime dt) {
        return fmtDateTime.print(dt);
    }

    public static String formatDateTimeRange(DateTime start, DateTime end) {
        if (start.equals(end)) {
            return String.format("%s %s", fmtDate.print(start), fmtTime.print(start));
        } else if (start.toLocalDate().equals(end.toLocalDate())) {
            return String.format("%s %s - %s", fmtDate.print(start), fmtTime.print(start), fmtTime.print(end));
        } else {
            return String.format("%s %s - %s %s", fmtDate.print(start), fmtTime.print(start), fmtDate.print(end), fmtTime.print(end));
        }
    }
}
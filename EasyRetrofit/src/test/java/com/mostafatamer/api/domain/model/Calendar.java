package com.mostafatamer.api.domain.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Calendar {
    @SerializedName("data")
    public List<CalendarData> data;

    // Getters and setters

    public static class CalendarData {
        @SerializedName("date")
        public DateInfo date;
        @SerializedName("timings")
        public Timings timings;

        // Getters and setters
    }

    public static class DateInfo {
        @SerializedName("gregorian")
        public GregorianDate gregorian;

        // Getters and setters
    }

    public static class GregorianDate {
        @SerializedName("date")
        public String date;

        // Getters and setters
    }

    public static class Timings {
        @SerializedName("Fajr")
        public String fajr;
        @SerializedName("Dhuhr")
        public String dhuhr;
        @SerializedName("Asr")
        public String asr;
        @SerializedName("Maghrib")
        public String maghrib;
        @SerializedName("Isha")
        public String isha;

        // Getters and setters
    }
}

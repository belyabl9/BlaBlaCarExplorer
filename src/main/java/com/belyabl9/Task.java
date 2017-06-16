package com.belyabl9;import java.util.Collections;
import java.util.List;

public class Task {
    private final String from;
    private final String to;
    private final String date;
    private final String fromCountry;
    private final String toCountry;
    private final String period;
    private final List<String> ignoreTripsLst;
    private final String outputFile;

    public Task(String from, String to, String date, String fromCountry, String toCountry, String period, List<String> ignoreTripsLst, String outputFile) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.fromCountry = fromCountry;
        this.toCountry = toCountry;
        this.period = period;
        this.ignoreTripsLst = ignoreTripsLst != null ? ignoreTripsLst : Collections.<String>emptyList();
        this.outputFile = outputFile;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDate() {
        return date;
    }

    public String getFromCountry() {
        return fromCountry;
    }

    public String getToCountry() {
        return toCountry;
    }

    public String getPeriod() {
        return period;
    }

    public List<String> getIgnoreTripsLst() {
        return ignoreTripsLst;
    }

    public String getOutputFile() {
        return outputFile;
    }
}

package com.belyabl9;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.TimerTask;

public class TripSearchTask extends TimerTask {

    @NotNull
    private final TripSearchStrategy searchStrategy;
    @NotNull
    private final Request request;

    private final long period;

    public TripSearchTask(@NotNull TripSearchStrategy searchStrategy, @NotNull Request request, long period) {
        this.searchStrategy = searchStrategy;
        this.request = request;
        this.period = period;
    }

    @Override
    public void run() {
        List<Trip> trips = searchStrategy.search(request);
        for (Trip trip : trips) {
            if (!searchStrategy.ignore(trip)) {
                searchStrategy.process(trip);
            }
        }
    }

    @NotNull
    public TripSearchStrategy getSearchStrategy() {
        return searchStrategy;
    }

    @NotNull
    public Request getRequest() {
        return request;
    }

    public long getPeriod() {
        return period;
    }
}

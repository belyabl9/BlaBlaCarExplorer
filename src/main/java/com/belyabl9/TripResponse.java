package com.belyabl9;

import java.util.List;

public class TripResponse {
    private List<Trip> trips;
    private Pager pager;

    public TripResponse(List<Trip> trips) {
        this.trips = trips;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

    @Override
    public String toString() {
        return "TripResponse{" +
                "trips=" + trips +
                ", pager=" + pager +
                '}';
    }
}

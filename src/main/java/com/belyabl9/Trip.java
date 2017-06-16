package com.belyabl9;

import com.google.gson.annotations.SerializedName;

public class Trip {
    @SerializedName("links")
    private Link link;
    @SerializedName("departure_date")
    private String departureDate;
    @SerializedName("departure_place")
    private Place departurePlace;
    @SerializedName("arrival_place")
    private Place arrivalPlace;

    public Trip(Link link, String departureDate, Place departurePlace, Place arrivalPlace) {
        this.link = link;
        this.departureDate = departureDate;
        this.departurePlace = departurePlace;
        this.arrivalPlace = arrivalPlace;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public Place getDeparturePlace() {
        return departurePlace;
    }

    public void setDeparturePlace(Place departurePlace) {
        this.departurePlace = departurePlace;
    }

    public Place getArrivalPlace() {
        return arrivalPlace;
    }

    public void setArrivalPlace(Place arrivalPlace) {
        this.arrivalPlace = arrivalPlace;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trip trip = (Trip) o;

        if (link != null ? !link.equals(trip.link) : trip.link != null) return false;
        if (departureDate != null ? !departureDate.equals(trip.departureDate) : trip.departureDate != null)
            return false;
        if (departurePlace != null ? !departurePlace.equals(trip.departurePlace) : trip.departurePlace != null)
            return false;
        return arrivalPlace != null ? arrivalPlace.equals(trip.arrivalPlace) : trip.arrivalPlace == null;
    }

    @Override
    public int hashCode() {
        int result = link != null ? link.hashCode() : 0;
        result = 31 * result + (departureDate != null ? departureDate.hashCode() : 0);
        result = 31 * result + (departurePlace != null ? departurePlace.hashCode() : 0);
        result = 31 * result + (arrivalPlace != null ? arrivalPlace.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "link=" + link +
                ", departureDate='" + departureDate + '\'' +
                ", departurePlace=" + departurePlace +
                ", arrivalPlace=" + arrivalPlace +
                '}';
    }
}

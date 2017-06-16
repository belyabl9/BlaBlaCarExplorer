package com.belyabl9;

import com.google.gson.annotations.SerializedName;

public class Place {

    @SerializedName("city_name")
    private String cityName;
    @SerializedName("country_code")
    private String countryCode;

    public Place(String cityName, String countryCode) {
        this.cityName = cityName;
        this.countryCode = countryCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (cityName != null ? !cityName.equals(place.cityName) : place.cityName != null) return false;
        return countryCode != null ? countryCode.equals(place.countryCode) : place.countryCode == null;
    }

    @Override
    public int hashCode() {
        int result = cityName != null ? cityName.hashCode() : 0;
        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Place{" +
                "cityName='" + cityName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}

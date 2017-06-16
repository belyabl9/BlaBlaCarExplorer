package com.belyabl9;import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

public class Request {

    public static final String MANDATORY_PARAM = "At least one parameter must be present.";

    public static class Builder {
        private String fromPlace;
        private String toPlace;
        private LocalDate date;
        private Integer page;

        public Builder fromPlace(String fromPlace) {
            this.fromPlace = fromPlace;
            return this;
        }

        public Builder toPlace(String toPlace) {
            this.toPlace = toPlace;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder page(Integer page) {
            this.page = page;
            return this;
        }

        public Request build() {
            if (fromPlace == null && toPlace == null && date == null) {
                throw new IllegalStateException(MANDATORY_PARAM);
            }

            return new Request(
                    fromPlace,
                    toPlace,
                    date != null ? date.toString() : null,
                    page != null ? String.valueOf(page) : null
            );
        }
    }

    @Nullable
    private String fromPlace;
    @Nullable
    private String toPlace;
    @Nullable
    private String date;
    @Nullable
    private String page;

    public Request(@Nullable String fromPlace, @Nullable String toPlace, @Nullable String date, @Nullable String page) {
        this.fromPlace = fromPlace;
        this.toPlace = toPlace;
        this.date = date;
        this.page = page;
    }

    @Nullable
    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(@Nullable String fromPlace) {
        this.fromPlace = fromPlace;
    }

    @Nullable
    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(@Nullable String toPlace) {
        this.toPlace = toPlace;
    }

    @Nullable
    public String getDate() {
        return date;
    }

    public void setDate(@Nullable String date) {
        this.date = date;
    }

    @Nullable
    public String getPage() {
        return page;
    }

    public void setPage(@Nullable String page) {
        this.page = page;
    }
}
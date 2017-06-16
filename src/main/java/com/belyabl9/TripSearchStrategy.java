package com.belyabl9;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TripSearchStrategy {

    List<Trip> search(@NotNull Request request);

    boolean ignore(@NotNull Trip trip);

    void process(@NotNull Trip trip);

}

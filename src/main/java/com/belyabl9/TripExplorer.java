package com.belyabl9;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TripExplorer {

    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClientBuilder.create().build();

    private static final Splitter IGNORE_TRIP_SPLITTER = Splitter.on(";");

    // BlaBlaCar trip links has the following format: "smth-1231231313"
    private static final Pattern ID_PATTERN = Pattern.compile("(\\d+)$");

    private static final Set<Trip> FOUND_TRIPS = new HashSet<>();

    private static final Pattern PERIOD_PATTERN = Pattern.compile("^(\\d+)(ms|s|m|h)");

    // 1h
    private static final long DEFAULT_PERIOD = 1_000 * 60 * 60;

    private static final String INVALID_TIME_PERIOD = "Can not parse time period.";
    private static final String TRIP_URL_NOT_SPECIFIED = "Trip URL must be specified.";
    private static final String TRIP_URL_MUST_MATCH_THE_PATTERN = "Trip URL must match the pattern. Otherwise adjust the pattern.";
    private static final String PERIOD_MUST_BE_SPECIFIED = "Period must be specified.";

    private final StartupParameters startupParams;

    public TripExplorer(@NotNull StartupParameters startupParams) {
        this.startupParams = startupParams;
    }


    public static void main(String[] args) throws URISyntaxException, IOException, ParseException {
        CommandLine cmd = processCmdParams(args);
        StartupParameters params = new StartupParameters(
                cmd.getOptionValue("method-url"),
                cmd.getOptionValue("key"),
                cmd.getOptionValue("tasks")
        );

        TripExplorer explorer = new TripExplorer(params);
        explorer.run();
    }

    @NotNull
    public List<Trip> getTrips(@NotNull Request request) {
        TripResponse tripResponse;
        try {
            tripResponse = makeRequest(makeUri(request, Optional.<Integer>absent()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        int pages = Integer.parseInt(tripResponse.getPager().getPages());
        if (pages == 0) {
            return Collections.emptyList();
        }

        List<Trip> trips = new ArrayList<>();
        int curPage = 1;
        while (!tripResponse.getTrips().isEmpty() && curPage <= pages) {
            trips.addAll(tripResponse.getTrips());
            curPage++;
            if (curPage < pages) {
                try {
                    tripResponse = makeRequest(makeUri(request, Optional.of(curPage)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return trips;
    }

    public void run() {
        List<TripSearchTask> tripSearchTasks = getTripSearchTasks();

        Timer timer = new Timer();
        for (TripSearchTask task : tripSearchTasks) {
            timer.scheduleAtFixedRate(task, 1_000 * 5, task.getPeriod());
        }
    }


    private URI makeUri(@NotNull Request request, Optional<Integer> page) throws URISyntaxException {
        URIBuilder uriBuilder = makeUriBuilder();
        if (page.isPresent()) {
            request.setPage(String.valueOf(page.get()));
        }
        setParameters(uriBuilder, request);

        return uriBuilder.build();
    }

    private static TripResponse makeRequest(URI uri) throws IOException {
        HttpGet httpRequest = new HttpGet(uri);
        HttpResponse response = HTTP_CLIENT.execute(httpRequest);
        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

        return GSON.fromJson(responseString, TripResponse.class);
    }

    private List<TripSearchTask> getTripSearchTasks() {
        File tasksDir = startupParams.getTaskDefDir();

        List<TripSearchTask> tasks = new ArrayList<>();
        for (File taskFile : tasksDir.listFiles()) {
            final Task task = readTask(taskFile);

            Request request = new Request.Builder()
                    .fromPlace(task.getFrom())
                    .toPlace(task.getTo())
                    .date(LocalDate.parse(task.getDate()))
                    .build();

            long period;
            if (task.getPeriod() != null) {
                period = parsePeriod(task.getPeriod());
            } else {
                period = DEFAULT_PERIOD;
            }

            TripSearchTask tripSearchTask = makeTripSearchTask(task, request, period);
            tasks.add(tripSearchTask);
        }

        return tasks;
    }

    @NotNull
    private TripSearchTask makeTripSearchTask(@NotNull final Task task, @NotNull Request request, long period) {
        return new TripSearchTask(
            new TripSearchStrategy() {
                @Override
                public List<Trip> search(@NotNull Request request) {
                    return searchAndFilterTrips(request, task);
                }

                @Override
                public boolean ignore(@NotNull Trip trip) {
                    return ignoreTrip(trip, task);
                }

                @Override
                public void process(@NotNull Trip trip) {
                    processTrip(trip, task);
                }
            },
            request,
            period
        );
    }

    private static void processTrip(@NotNull Trip trip, @NotNull Task task) {
        if (task.getOutputFile() == null) {
            return;
        }
        if (FOUND_TRIPS.contains(trip)) {
            return;
        }
        File outputFile = new File(task.getOutputFile());
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            writer.write(trip.toString());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FOUND_TRIPS.add(trip);
    }

    private static boolean ignoreTrip(@NotNull Trip trip, @NotNull Task task) {
        if (task.getIgnoreTripsLst().isEmpty()) {
            return false;
        }
        String url = trip.getLink().getUrl();
        if (url.isEmpty()) {
            throw new IllegalArgumentException(TRIP_URL_NOT_SPECIFIED);
        }
        Matcher matcher = ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return task.getIgnoreTripsLst().contains(matcher.group(1));
        }
        throw new IllegalArgumentException(TRIP_URL_MUST_MATCH_THE_PATTERN);
    }

    @NotNull
    private List<Trip> searchAndFilterTrips(@NotNull Request request, @NotNull Task task) {
        List<Trip> trips = getTrips(request);
        if (trips.isEmpty()) {
            return Collections.emptyList();
        }

        List<Trip> resultTrips = new ArrayList<>();
        for (Trip trip : trips) {
            if (task.getFromCountry() != null) {
                if (trip.getDeparturePlace().getCountryCode().equals(task.getFromCountry())) {
                    resultTrips.add(trip);
                }
            } else if (task.getToCountry() != null) {
                if (trip.getArrivalPlace().getCountryCode().equals(task.getToCountry())) {
                    resultTrips.add(trip);
                }
            }
        }

        return resultTrips;
    }

    private static long parsePeriod(@NotNull String period) {
        if (period.isEmpty()) {
            throw new IllegalArgumentException(PERIOD_MUST_BE_SPECIFIED);
        }

        Matcher matcher = PERIOD_PATTERN.matcher(period);
        if (matcher.find()) {
            long value;
            try {
                value = Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                throw new RuntimeException(INVALID_TIME_PERIOD, e);
            }
            String timeType = matcher.group(2);

            switch (timeType) {
                case "ms":
                    return value;
                case "s":
                    return value * 1000;
                case "m":
                    return value * 1000 * 60;
                case "h":
                    return value * 1000 * 60 * 60;
                default:
                    throw new UnsupportedOperationException("Specified time type is not supported: " + timeType);
            }
        }
        throw new IllegalArgumentException(INVALID_TIME_PERIOD);
    }

    @NotNull
    private static Task readTask(File taskFile) {
        Properties prop = new Properties();
        try (InputStream inputStream = new FileInputStream(taskFile)) {
            prop.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String from = prop.getProperty("from");
        String to = prop.getProperty("to");
        String date = prop.getProperty("date");
        String fromCountry = prop.getProperty("from_country");
        String toCountry = prop.getProperty("to_country");
        String period = prop.getProperty("period");
        String ignoreTrips = prop.getProperty("ignore_trips");
        List<String> ignoreTripsLst = ignoreTrips != null ? IGNORE_TRIP_SPLITTER.splitToList(ignoreTrips) : Collections.<String>emptyList();

        String outputFile = prop.getProperty("output_file");

        return new Task(from, to, date, fromCountry, toCountry, period, ignoreTripsLst, outputFile);
    }

    private static CommandLine processCmdParams(String[] args) throws ParseException {
        Options options = new Options();

        options.addOption("method-url", true, "URL of API method for retrieving trips");
        options.addOption("key", true, "API key");
        options.addOption("tasks", true, "Path to directory with task definitions");

        CommandLineParser parser = new GnuParser();
        return parser.parse(options, args);
    }

    private URIBuilder makeUriBuilder() {
        URI methodUrl = startupParams.getMethodUrl();
        return new URIBuilder()
                        .setScheme(methodUrl.getScheme())
                        .setHost(methodUrl.getHost())
                        .setPath(methodUrl.getPath())
                        .setParameter("key", startupParams.getKey());
    }

    private static void setParameters(@NotNull URIBuilder builder, @NotNull Request request) {
        // TODO set as input param
        builder.setParameter("locale", "uk_UA");

        if (request.getFromPlace() != null) {
            builder.setParameter("fn", request.getFromPlace());
        }
        if (request.getToPlace() != null) {
            builder.setParameter("tn", request.getToPlace());
        }
        if (request.getDate() != null) {
            builder.setParameter("db", request.getDate());
        }
        if (request.getPage() != null) {
            builder.setParameter("page", request.getPage());
        }
    }

}

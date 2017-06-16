package com.belyabl9;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartupParameters {
    private static final String API_METHOD_URL_NOT_SPECIFIED = "API method URL must be specified.";
    private static final String API_KEY_NOT_SPECIFIED = "API key must be specified.";
    private static final String TASK_DEFINITIONS_PATH_NOT_SPECIFIED = "Task definitions path must be specified.";
    private static final String TASK_DEFINITIONS_DIR_MUST_EXIST = "Task definitions directory must exist.";

    private static final Pattern URI_PATTERN = Pattern.compile("^(htt[ps])://(.*)(/api/v\\d+/.*)$", Pattern.CASE_INSENSITIVE);
    private static final String INVALID_API_METHOD_URL = "API method URL is invalid.";

    @NotNull
    private final File taskDefDir;
    @NotNull
    private final String key;
    @NotNull
    private final URI methodUrl;

    public StartupParameters(@NotNull String methodUrl, @NotNull String key, @NotNull String taskDefPath) {
        validate(methodUrl, key, taskDefPath);

        try {
            this.methodUrl = new URI(methodUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.key = key;
        this.taskDefDir = new File(taskDefPath);
    }

    @NotNull
    public File getTaskDefDir() {
        return taskDefDir;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public URI getMethodUrl() {
        return methodUrl;
    }

    private void validate(@NotNull String methodUrl, @NotNull String key, @NotNull String taskDefPath) {
        if (methodUrl.isEmpty()) {
            throw new IllegalArgumentException(API_METHOD_URL_NOT_SPECIFIED);
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException(API_KEY_NOT_SPECIFIED);
        }
        if (taskDefPath.isEmpty()) {
            throw new IllegalArgumentException(TASK_DEFINITIONS_PATH_NOT_SPECIFIED);
        }
        File taskDefDir = new File(taskDefPath);
        if (!taskDefDir.exists() || !taskDefDir.isDirectory()) {
            throw new IllegalArgumentException(TASK_DEFINITIONS_DIR_MUST_EXIST);
        }

        Matcher uriMatcher = URI_PATTERN.matcher(methodUrl);
        if (!uriMatcher.find()) {
            throw new IllegalArgumentException(INVALID_API_METHOD_URL);
        }
    }
}

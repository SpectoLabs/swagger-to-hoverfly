package io.specto.swagger.hoverfly;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RequestDetails {
    private final String requestType = "recording";
    private final String path;
    private final String method;
    private final String destination;
    private final String scheme;
    private final String query;
    private final String body;
    private final Map<String, List<String>> headers;

    private RequestDetails(String path, String method, String destination, String scheme, String query, String body, Map<String, List<String>> headers) {
        this.path = path;
        this.method = method;
        this.destination = destination;
        this.scheme = scheme;
        this.query = query;
        this.body = body;
        this.headers = headers;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getDestination() {
        return destination;
    }

    public String getScheme() {
        return scheme;
    }

    public String getQuery() {
        return query;
    }

    public String getBody() {
        return body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return method + " " + destination + path;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {

        private static final String SCHEME = "http";
        private String path = "";
        private String method = "";
        private String destination;
        private String body = "";
        private String query = "";

        public Builder withPath(final String path) {
            this.path = path;
            return this;
        }

        public Builder withMethod(final String method) {
            this.method = method;
            return this;
        }

        public Builder withDestination(final String destination) {
            this.destination = destination;
            return this;
        }

        public Builder withBody(final String body) {
            this.body = body;
            return this;
        }

        public RequestDetails build() {
            return new RequestDetails(path, method, destination, SCHEME, query, body, Collections.emptyMap());
        }

        public Builder withQuery(final String query) {
            this.query = query;
            return this;
        }
    }
}
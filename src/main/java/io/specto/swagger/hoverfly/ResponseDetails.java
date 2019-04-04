package io.specto.swagger.hoverfly;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResponseDetails {
    private final int status;
    private final String body;
    private final boolean encodedBody;
    private final Map<String, List<String>> headers;

    private ResponseDetails(int status, String body, boolean encodedBody, Map<String, List<String>> headers) {
        this.status = status;
        this.body = body;
        this.encodedBody = encodedBody;
        this.headers = headers;
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public boolean isEncodedBody() {
        return encodedBody;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {

        private String body = "";
        private int status = 200;
        Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();

        public Builder withBody(final String body) {
            this.body = body;
            return this;
        }

        public Builder withHeaders(final Map<String, List<String>> headers) {
            this.headers = headers;
            return this;
        }
        
        public Builder addHeader(final String key, List<String> values) {
            this.headers.put(key, values);
            return this;
        }

        public ResponseDetails build() {
            return new ResponseDetails(status, body, false, headers);
        }

        public Builder withStatus(final int status) {
            this.status = status;
            return this;
        }
    }
}
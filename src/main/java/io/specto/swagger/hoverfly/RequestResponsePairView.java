package io.specto.swagger.hoverfly;

public class RequestResponsePairView {
    private final RequestDetails request;
    private final ResponseDetails response;

    public RequestResponsePairView(RequestDetails request, ResponseDetails response) {
        this.request = request;
        this.response = response;
    }

    public RequestDetails getRequest() {
        return request;
    }

    public ResponseDetails getResponse() {
        return response;
    }
}
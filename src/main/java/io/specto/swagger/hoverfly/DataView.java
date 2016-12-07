package io.specto.swagger.hoverfly;

import java.util.List;

public class DataView {
    private final List<RequestResponsePairView> pairs;

    public DataView(List<RequestResponsePairView> pairs) {
        this.pairs = pairs;
    }

    public List<RequestResponsePairView> getPairs() {
        return pairs;
    }
}
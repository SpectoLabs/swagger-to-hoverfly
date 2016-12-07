package io.specto.swagger.hoverfly;

public class PayloadView {
    private final DataView data;
    private final MetaView meta;

    public PayloadView(DataView data, MetaView meta) {
        this.data = data;
        this.meta = meta;
    }

    public DataView getData() {
        return data;
    }

    public MetaView getMeta() {
        return meta;
    }
}
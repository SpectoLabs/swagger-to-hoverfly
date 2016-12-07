package io.specto.swagger;

import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;

public class PropertyValueGenerator {
    public String generate(final Property baseProperty) {

        if (baseProperty instanceof MapProperty) {
            return "{}";
        }

        if (baseProperty.getType().equals("integer")) {
            return "0";
        }

        if (baseProperty.getExample() != null) {
            return baseProperty.getExample().toString();
        }

        if (baseProperty instanceof StringProperty) {
            StringProperty p = (StringProperty) baseProperty;

            return p.getEnum() != null && !p.getEnum().isEmpty() ? p.getEnum().get(0) : "string";
        }

        return "";
    }
}

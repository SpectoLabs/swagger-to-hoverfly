package io.specto.swagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

import java.util.Map;

public class JsonNodeGenerator {

    private final Map<String, Model> definitions;
    private final JsonNodeFactory factory = JsonNodeFactory.instance;
    private final PropertyValueGenerator propertyValueGenerator = new PropertyValueGenerator();

    public JsonNodeGenerator(Map<String, Model> definitions) {
        this.definitions = definitions;
    }

    public JsonNode generate(final Property baseProperty) {

        if (baseProperty.getType() != null && baseProperty.getType().equals("ref")) {
            RefProperty refProperty = (RefProperty) baseProperty;
            Model model = definitions.get(refProperty.getSimpleRef());
            if(model.getExample() != null) {
                ObjectNode node = (ObjectNode) model.getExample();
                return node.without("");
            } else {
                if(model.getClass().getName() == "io.swagger.models.ArrayModel") {
                    ArrayModel arrayModel = (ArrayModel) model;
                    Property property = arrayModel.getItems();
                    JsonNode innerNode = generate(property);
                    ArrayNode outerNode = new ArrayNode(factory);
                    outerNode.add(innerNode);
                    return outerNode;
                } 
                return generateJsonObject(model.getProperties());
            }
        }

        if (baseProperty.getType() != null && baseProperty.getType().equals("object") && !(baseProperty instanceof MapProperty)) {
            ObjectProperty objectProperty = (ObjectProperty) baseProperty;
            return generateJsonObject(objectProperty.getProperties());
        }

        if (baseProperty.getType() != null && baseProperty.getType().contains("array")) {
            return generateJsonArray((ArrayProperty) baseProperty);
        }

        return factory.textNode(propertyValueGenerator.generate(baseProperty));
    }

    private JsonNode generateJsonArray(ArrayProperty arrayProperty) {
        Property itemProperty = arrayProperty.getItems();
        ArrayNode array = factory.arrayNode();
        array.add(generate(itemProperty));
        return array;
    }

    private JsonNode generateJsonObject(Map<String, Property> properties) {

        ObjectNode root = factory.objectNode();
        if(properties == null) {
            return root;
        }

        for (Map.Entry<String, Property> propertyEntry : properties.entrySet()) {
            root.set(propertyEntry.getKey(), generate(propertyEntry.getValue()));
        }
        return root;
    }

}

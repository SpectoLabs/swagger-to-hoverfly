package io.specto.swagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.specto.swagger.hoverfly.DataView;
import io.specto.swagger.hoverfly.MetaView;
import io.specto.swagger.hoverfly.PayloadView;
import io.specto.swagger.hoverfly.RequestDetails;
import io.specto.swagger.hoverfly.RequestResponsePairView;
import io.specto.swagger.hoverfly.ResponseDetails;
import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class SwaggerConverter {

    private static final String EMPTY_REQUEST_BODY = "";
    private final ObjectMapper mapper = new ObjectMapper();
    private final PropertyValueGenerator propertyValueGenerator = new PropertyValueGenerator();

    public PayloadView convert(String swaggerFile) {
        Swagger swagger = new SwaggerParser().read(swaggerFile);

        final List<RequestResponsePairView> payloadList = swagger.getPaths().entrySet()
                .stream()
                .flatMap(e -> convertOperations(swagger, e))
                .collect(toList());

        return new PayloadView(new DataView(payloadList), new MetaView());
    }

    private Stream<RequestResponsePairView> convertOperations(final Swagger swagger, final Map.Entry<String, Path> e) {
        return e.getValue().getOperationMap().entrySet()
                .stream()
                .map(operationEntry -> convertOperation(e.getKey(), swagger, operationEntry));
    }

    private RequestResponsePairView convertOperation(final String urlPath, final Swagger swagger, final Map.Entry<HttpMethod, Operation> operationEntry) {

        final RequestDetails requestDetails = RequestDetails.Builder()
                .withPath(swagger.getBasePath() + urlPath)
                .withMethod(operationEntry.getKey().name())
                .withDestination(swagger.getHost())
                .withQuery(generateQueryParameters(operationEntry.getValue()))
                .withBody(generateRequestBody(operationEntry.getValue(), swagger.getDefinitions()))
                .build();

        final Map<String, Response> responses = operationEntry.getValue().getResponses();

        // Get 200 response, otherwise just pick the first one
        final Map.Entry<String, Response> response = responses.entrySet().stream()
                .filter(r -> responses.containsKey("200"))
                .findFirst()
                .orElseGet(() -> responses.entrySet().iterator().next());

        final ResponseDetails.Builder responseBuilder = ResponseDetails.Builder()
                .withStatus(StringUtils.isNumeric(response.getKey()) ? Integer.parseInt(response.getKey()) : 200);

        if (response.getValue().getSchema() != null) {
            responseBuilder.withBody(tryAndConvertToJsonBody(swagger.getDefinitions(), response.getValue().getSchema()));
        }

        return new RequestResponsePairView(requestDetails, responseBuilder.build());
    }

    private String generateQueryParameters(final Operation operation) {

        final String query = operation.getParameters().stream()
                .filter(p -> p instanceof QueryParameter)
                .map(p -> (QueryParameter) p)
                .map(p -> {
                    if (p.getType().equals("array")) {
                        return p.getName() + "=" + propertyValueGenerator.generate(p.getItems());
                    }
                    return "";
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));

        return query.isEmpty() ? "" : "?" + query;
    }

    private String generateRequestBody(final Operation operation, final Map<String, Model> definitions) {

        return  operation.getParameters().stream()
                .filter(p -> p instanceof BodyParameter)
                .map(p -> (BodyParameter) p)
                .map(BodyParameter::getSchema)
                .filter(p -> p instanceof RefModel)
                .map(r -> (RefModel) r)
                .map(r -> new RefProperty(r.get$ref()))
                .map(p -> tryAndConvertToJsonBody(definitions, p))
                .findFirst()
                .orElse(EMPTY_REQUEST_BODY);
    }

    private String tryAndConvertToJsonBody(final Map<String, Model> definitions, final Property baseProperty) {
        JsonNode jsonResponseNode = new JsonNodeGenerator(definitions).generate(baseProperty);

        try {
            return mapper.writeValueAsString(jsonResponseNode);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }
}
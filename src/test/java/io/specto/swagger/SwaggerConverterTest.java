package io.specto.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.specto.swagger.hoverfly.PayloadView;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.net.URL;

import static com.cedarsoftware.util.io.JsonWriter.formatJson;
import static java.nio.charset.Charset.defaultCharset;


public class SwaggerConverterTest {

    @Test
    public void shouldConvertSimpleSwaggerYamlToHoverflyJson() throws Exception {
        testConversion("simple-swagger.yaml", "simple-swagger-output.json");
    }

    @Test
    public void shouldConvertPetstoreSwaggerYamlToHoverflyJson() throws Exception {
        testConversion("petstore.yaml", "petstore-output.json");
    }

    private void testConversion(final String from, final String to) throws IOException, JSONException {
        URL swaggerYamlUrl = Resources.getResource(from);
        URL hoverflyJsonUrl = Resources.getResource(to);
        String expectedJson = Resources.toString(hoverflyJsonUrl, defaultCharset());
        PayloadView data = new SwaggerConverter().convert(swaggerYamlUrl.getPath());

        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(data);

        System.out.println(formatJson(actualJson));

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }
}

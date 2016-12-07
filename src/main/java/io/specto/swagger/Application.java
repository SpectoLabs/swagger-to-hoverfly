package io.specto.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.specto.swagger.hoverfly.PayloadView;

import java.nio.file.Paths;

public class Application {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Invalid arguments count:" + args.length);
            System.exit(1);
        }
        String filePath = Paths.get(args[0]).toString();
        PayloadView view = new SwaggerConverter().convert(filePath);
        try {
            String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(view);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to process json with error: " + e.getMessage());
            System.exit(1);
        }
    }
}

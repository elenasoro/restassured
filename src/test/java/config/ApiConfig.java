package config;

import io.qameta.allure.Allure;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ApiConfig {

    public static RequestSpecification requestSpec() {
        String apiKey = apiKey();

        return new RequestSpecBuilder()
                .setBaseUri(baseUri())
                .setBasePath("/api")
                .setContentType(ContentType.JSON)
                .addHeader("x-api-key", apiKey.strip())
                .addFilter((request, response, context) -> {
                    Allure.addAttachment("HTTP request", "text/plain",
                            request.getMethod() + " " + request.getURI());
                    Response actualResponse = context.next(request, response);
                    Allure.addAttachment("HTTP response", "text/plain", actualResponse.getStatusLine());
                    return actualResponse;
                })
                .setConfig(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", 5_000)
                                .setParam("http.socket.timeout", 10_000)
                                .setParam("http.conn-manager.timeout", 5_000L))
                        .logConfig(LogConfig.logConfig()
                                .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.HEADERS)
                                .blacklistHeader("Authorization", "x-api-key", "Cookie", "Set-Cookie")))
                .build();
    }

    private static String baseUri() {
        String baseUri = System.getProperty("api.baseUri");
        if (baseUri == null) {
            Properties properties = new Properties();
            try (InputStream input = ApiConfig.class.getResourceAsStream("/config.properties")) {
                if (input == null) {
                    throw new IllegalStateException("Cannot find config.properties in the classpath.");
                }
                properties.load(input);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read config.properties.", e);
            }
            baseUri = properties.getProperty("api.baseUri");
        }

        if (baseUri == null || baseUri.isBlank()) {
            throw new IllegalStateException("Set api.baseUri in config.properties or via -Dapi.baseUri.");
        }
        return baseUri.strip();
    }

    private static String apiKey() {
        String apiKey = System.getenv("REQRES_API_KEY");
        if (apiKey != null && !apiKey.isBlank()) {
            return apiKey.strip();
        }

        Path configPath = Path.of("config.local.properties");
        if (Files.exists(configPath)) {
            Properties properties = new Properties();
            try (Reader reader = Files.newBufferedReader(configPath)) {
                properties.load(reader);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read " + configPath.toAbsolutePath(), e);
            }
            apiKey = properties.getProperty("reqres.api.key");
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Set REQRES_API_KEY or reqres.api.key in "
                    + configPath.toAbsolutePath() + " before running tests.");
        }
        return apiKey.strip();
    }
}

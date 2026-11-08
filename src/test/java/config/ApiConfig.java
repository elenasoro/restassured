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

public class ApiConfig {

    public static RequestSpecification requestSpec() {
        String apiKey = System.getenv("REQRES_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Set the REQRES_API_KEY environment variable before running tests.");
        }

        return new RequestSpecBuilder()
                .setBaseUri(System.getProperty("api.baseUri", "https://reqres.in"))
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
}

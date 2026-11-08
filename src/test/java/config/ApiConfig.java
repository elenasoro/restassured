package config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiConfig {

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(System.getProperty("api.baseUri", "https://reqres.in"))
                .setBasePath("/api")
                .setContentType(ContentType.JSON)
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

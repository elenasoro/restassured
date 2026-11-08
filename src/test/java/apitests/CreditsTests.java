package apitests;

import config.TestConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.CreditBalanceResponse;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CreditsTests {

    @Test(description = "TC-01: Get the current PRO user's credit balance")
    public void shouldGetCurrentUserCreditBalance() {
        String baseUri = TestConfig.baseUri();
        String accessToken = TestConfig.accessToken();
        long expectedUserId = TestConfig.userId();

        CreditBalanceResponse response = given()
                .baseUri(baseUri)
                .basePath("/api/v1")
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .config(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", 5_000)
                                .setParam("http.socket.timeout", 10_000)
                                .setParam("http.conn-manager.timeout", 5_000L))
                        .logConfig(LogConfig.logConfig()
                                .blacklistHeader("Authorization", "Cookie", "Set-Cookie")))
                .filter(new RequestLoggingFilter(LogDetail.ALL))
                .filter(new ResponseLoggingFilter(LogDetail.ALL))
                .when()
                .get("/credits/balance")
                .then()
                .statusCode(200)
                .extract()
                .as(CreditBalanceResponse.class, ObjectMapperType.JACKSON_2);

        assertThat(response.getUserId()).as("user_id").isEqualTo(expectedUserId);
        assertThat(response.getSubscription()).as("subscription").isEqualTo("PRO");
        assertThat(response.getBalance()).as("balance").isEqualTo(1000);
        assertThat(response.getCurrency()).as("currency").isEqualTo("CREDITS");
    }

    @Test(description = "TC-04: Reject an influencer request without charging when credits are insufficient")
    public void shouldRejectInfluencerRequestWhenCreditsAreInsufficient() {
        String baseUri = TestConfig.baseUri();
        String accessToken = TestConfig.lowBalanceAccessToken();
        long expectedUserId = TestConfig.lowBalanceUserId();
        String influencerId = TestConfig.influencerId();
        String idempotencyKey = UUID.randomUUID().toString();

        RequestSpecification requestSpec = given()
                .baseUri(baseUri)
                .basePath("/api/v1")
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .config(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", 5_000)
                                .setParam("http.socket.timeout", 10_000)
                                .setParam("http.conn-manager.timeout", 5_000L))
                        .logConfig(LogConfig.logConfig()
                                .blacklistHeader("Authorization", "Cookie", "Set-Cookie")))
                .filter(new RequestLoggingFilter(LogDetail.ALL))
                .filter(new ResponseLoggingFilter(LogDetail.ALL));

        CreditBalanceResponse balanceBefore = given()
                .spec(requestSpec)
                .when()
                .get("/credits/balance")
                .then()
                .statusCode(200)
                .extract()
                .as(CreditBalanceResponse.class, ObjectMapperType.JACKSON_2);

        assertThat(balanceBefore.getUserId()).as("initial user_id").isEqualTo(expectedUserId);
        assertThat(balanceBefore.getSubscription()).as("initial subscription").isEqualTo("PRO");
        assertThat(balanceBefore.getBalance()).as("initial balance").isEqualTo(5);

        JsonPath errorResponse = given()
                .spec(requestSpec)
                .header("Idempotency-Key", idempotencyKey)
                .pathParam("id", influencerId)
                .when()
                .get("/influencers/{id}")
                .then()
                .statusCode(402)
                .extract()
                .jsonPath();

        assertThat(errorResponse.getString("error")).as("error").isEqualTo("INSUFFICIENT_CREDITS");
        assertThat(errorResponse.getString("message")).as("message")
                .isEqualTo("Not enough credits to perform this request");
        assertThat(errorResponse.getInt("required_credits")).as("required_credits").isEqualTo(10);
        assertThat(errorResponse.getInt("available_credits")).as("available_credits").isEqualTo(5);

        CreditBalanceResponse balanceAfter = given()
                .spec(requestSpec)
                .when()
                .get("/credits/balance")
                .then()
                .statusCode(200)
                .extract()
                .as(CreditBalanceResponse.class, ObjectMapperType.JACKSON_2);

        assertThat(balanceAfter.getUserId()).as("final user_id").isEqualTo(expectedUserId);
        assertThat(balanceAfter.getBalance()).as("balance after rejected request").isEqualTo(5);
    }

    @Test(description = "TC-14: Concurrent requests with the same key charge credits only once")
    public void shouldChargeCreditsOnceForConcurrentRequestsWithSameKey() throws Exception {
        String baseUri = TestConfig.baseUri();
        String accessToken = TestConfig.concurrentAccessToken();
        long expectedUserId = TestConfig.concurrentUserId();
        String influencerId = TestConfig.influencerId();
        String idempotencyKey = UUID.randomUUID().toString();
        Properties dbProperties = new Properties();
        dbProperties.setProperty("user", TestConfig.dbUser());
        dbProperties.setProperty("password", TestConfig.dbPassword());
        dbProperties.setProperty("connectTimeout", "5");
        dbProperties.setProperty("socketTimeout", "10");

        try (Connection connection = DriverManager.getConnection(TestConfig.dbUrl(), dbProperties)) {
            connection.setReadOnly(true);

            CreditBalanceResponse balanceBefore = given()
                    .spec(concurrentRequestSpec(baseUri, accessToken))
                    .when()
                    .get("/credits/balance")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(CreditBalanceResponse.class, ObjectMapperType.JACKSON_2);

            assertThat(balanceBefore.getUserId()).as("initial user_id").isEqualTo(expectedUserId);
            assertThat(balanceBefore.getSubscription()).as("initial subscription").isEqualTo("PRO");
            assertThat(balanceBefore.getBalance()).as("initial balance").isEqualTo(1000);
            assertThat(transactionAmounts(connection, expectedUserId, idempotencyKey))
                    .as("transactions for the new idempotency key before requests").isEmpty();

            // The barrier synchronizes client dispatch; server-side overlap needs separate evidence.
            CyclicBarrier startBarrier = new CyclicBarrier(3);
            ExecutorService executor = Executors.newFixedThreadPool(2);
            List<Future<Response>> futures = new ArrayList<>();
            List<Response> responses = new ArrayList<>();
            try {
                for (int i = 0; i < 2; i++) {
                    futures.add(executor.submit(() -> {
                        RequestSpecification request = given()
                                .spec(concurrentRequestSpec(baseUri, accessToken))
                                .header("Idempotency-Key", idempotencyKey)
                                .pathParam("id", influencerId);
                        startBarrier.await(10, TimeUnit.SECONDS);
                        return request.when().get("/influencers/{id}");
                    }));
                }

                startBarrier.await(10, TimeUnit.SECONDS);
                for (Future<Response> future : futures) {
                    responses.add(future.get(20, TimeUnit.SECONDS));
                }
            } finally {
                for (Future<Response> future : futures) {
                    future.cancel(true);
                }
                executor.shutdownNow();
                assertThat(executor.awaitTermination(15, TimeUnit.SECONDS))
                        .as("request threads terminated").isTrue();
            }

            for (int i = 0; i < responses.size(); i++) {
                assertThat(responses.get(i).statusCode())
                        .as("HTTP status of concurrent request %s", i + 1).isEqualTo(200);
            }

            CreditBalanceResponse balanceAfter = given()
                    .spec(concurrentRequestSpec(baseUri, accessToken))
                    .when()
                    .get("/credits/balance")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(CreditBalanceResponse.class, ObjectMapperType.JACKSON_2);

            assertThat(balanceAfter.getUserId()).as("final user_id").isEqualTo(expectedUserId);
            assertThat(balanceAfter.getBalance()).as("balance after concurrent requests").isEqualTo(990);
            List<BigDecimal> amounts = transactionAmounts(connection, expectedUserId, idempotencyKey);
            assertThat(amounts).as("transactions for the user and idempotency key").hasSize(1);
            assertThat(amounts.get(0)).as("single transaction amount").isEqualByComparingTo("-10");
        }
    }

    private RequestSpecification concurrentRequestSpec(String baseUri, String accessToken) {
        return given()
                .baseUri(baseUri)
                .basePath("/api/v1")
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .config(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", 5_000)
                                .setParam("http.socket.timeout", 10_000)
                                .setParam("http.conn-manager.timeout", 5_000L))
                        .logConfig(LogConfig.logConfig()
                                .blacklistHeader("Authorization", "Cookie", "Set-Cookie")))
                .filter(new RequestLoggingFilter(LogDetail.ALL))
                .filter(new ResponseLoggingFilter(LogDetail.ALL));
    }

    private List<BigDecimal> transactionAmounts(Connection connection, long userId, String idempotencyKey)
            throws SQLException {
        String sql = "SELECT amount FROM credit_transactions WHERE user_id = ? AND idempotency_key::text = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setQueryTimeout(5);
            statement.setLong(1, userId);
            statement.setString(2, idempotencyKey);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<BigDecimal> amounts = new ArrayList<>();
                while (resultSet.next()) {
                    amounts.add(resultSet.getBigDecimal("amount"));
                }
                return amounts;
            }
        }
    }
}

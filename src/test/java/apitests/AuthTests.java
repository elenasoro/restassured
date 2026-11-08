package apitests;

import config.ApiConfig;
import models.AuthRequest;
import org.testng.annotations.Test;
import testdata.AuthTestData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTests {

    @Test
    public void registerUserSuccessfulTest() {
        AuthRequest registerBody = AuthRequest
                .builder()
                .email(AuthTestData.EMAIL)
                .password(AuthTestData.PASSWORD_FOR_REGISTER)
                .build();

        given()
                .spec(ApiConfig.requestSpec())
                .body(registerBody)
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body("id", not(equalTo(null)))
                .body("token", not(equalTo(null)));
    }

    @Test
    public void registerUserUnsuccessfulTest() {
        AuthRequest registerBody = AuthRequest
                .builder()
                .email(AuthTestData.EMAIL)
                .build();

        given()
                .spec(ApiConfig.requestSpec())
                .body(registerBody)
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test
    public void loginSuccessfulTest() {
        AuthRequest loginBody = AuthRequest
                .builder()
                .email(AuthTestData.EMAIL)
                .password(AuthTestData.PASSWORD_FOR_LOGIN)
                .build();

        given()
                .spec(ApiConfig.requestSpec())
                .body(loginBody)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", not(equalTo(null)));
    }

    @Test
    public void loginUnsuccessfulTest() {
        AuthRequest loginBody = AuthRequest
                .builder()
                .email(AuthTestData.EMAIL)
                .build();

        given()
                .spec(ApiConfig.requestSpec())
                .body(loginBody)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

}

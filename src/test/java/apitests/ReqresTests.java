package apitests;

import constants.Credentials;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import models.RegisterUserModel;
import models.UpdateUserModel;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

public class ReqresTests {

    @Test
    public void updateUserTest() {
        UpdateUserModel updateBody = UpdateUserModel
                .builder()
                .name("Lena")
                .job("QA")
                .build();

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .and()
                .body(updateBody)
                .when()
                .patch("https://reqres.in/api/users/2")
                .then()
                .statusCode(200)
                .body("name", equalTo("Lena"))
                .body("job", equalTo("QA"));
    }

    @Test
    public void deleteUserTest() {
        RestAssured
                .given()
                .when()
                .delete("https://reqres.in/api/users/2")
                .then()
                .statusCode(204);
    }

    @Test
    public void registerUserSuccessfulTest() {
        RegisterUserModel registerBody = RegisterUserModel
                .builder()
                .email(Credentials.EMAIL)
                .password(Credentials.PASSWORD_FOR_REGISTER)
                .build();

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .and()
                .body(registerBody)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .statusCode(200)
                .body("id", not(equalTo(null)))
                .body("token", not(equalTo(null)));
    }

    @Test
    public void registerUserUnsuccessfulTest() {
        RegisterUserModel registerBody = RegisterUserModel
                .builder()
                .email(Credentials.EMAIL)
                .build();

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .and()
                .body(registerBody)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test
    public void loginSuccessfulTest() {
        RegisterUserModel loginBody = RegisterUserModel
                .builder()
                .email(Credentials.EMAIL)
                .password(Credentials.PASSWORD_FOR_LOGIN)
                .build();

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .and()
                .body(loginBody)
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .statusCode(200)
                .body("token", not(equalTo(null)));
    }

    @Test
    public void loginUnsuccessfulTest() {
        RegisterUserModel loginBody = RegisterUserModel
                .builder()
                .email(Credentials.EMAIL)
                .build();

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .and()
                .body(loginBody)
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test
    public void delayResponseTest() {
        RestAssured
                .given()
                .queryParam("delay", "3")
                .when()
                .get("https://reqres.in/api/users")
                .then()
                .statusCode(200);
    }


}

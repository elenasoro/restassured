package apitests;

import config.ApiConfig;
import constants.Credentials;
import models.RegisterUserModel;
import models.UpdateUserModel;
import models.UserDTO;
import models.UserResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.*;

public class ReqresTests {

    @Test
    public void getUserByIdTest() {

        UserDTO expectedUser = UserDTO.builder()
                .id(2)
                .email("janet.weaver@reqres.in")
                .firstName("Janet")
                .lastName("Weaver")
                .avatar("https://reqres.in/img/faces/2-image.jpg")
                .build();

        UserResponse response = given()
                .spec(ApiConfig.requestSpec())
                .when()
                .get("/users/{id}", 2)
                .then()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        assertThat(response.getData())
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }

    @DataProvider(name = "invalidUserIds")
    public Object[][] invalidUserIds() {
        return new Object[][]{
                {"0"},
                {"-1"},
                {"abc"},
                {"2.5"}
        };
    }

    @Test(dataProvider = "invalidUserIds")
    public void getUserByInvalidIdTest(String id) {
        given()
                .spec(ApiConfig.requestSpec())
                .when()
                .get("/users/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    public void updateUserTest() {
        UpdateUserModel updateBody = UpdateUserModel
                .builder()
                .name("Lena")
                .job("QA")
                .build();

        given()
                .spec(ApiConfig.requestSpec())
                .body(updateBody)
                .when()
                .patch("/users/{id}", 2)
                .then()
                .statusCode(200)
                .body("name", equalTo(updateBody.getName()))
                .body("job", equalTo(updateBody.getJob()));
    }

    @Test
    public void deleteUserTest() {
        given()
                .spec(ApiConfig.requestSpec())
                .when()
                .delete("/users/{id}", 2)
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
        RegisterUserModel registerBody = RegisterUserModel
                .builder()
                .email(Credentials.EMAIL)
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
        RegisterUserModel loginBody = RegisterUserModel
                .builder()
                .email(Credentials.EMAIL)
                .password(Credentials.PASSWORD_FOR_LOGIN)
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
        RegisterUserModel loginBody = RegisterUserModel
                .builder()
                .email(Credentials.EMAIL)
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

    @Test
    public void delayResponseTest() {
        given()
                .spec(ApiConfig.requestSpec())
                .queryParam("delay", "3")
                .when()
                .get("/users")
                .then()
                .statusCode(200);
    }


}

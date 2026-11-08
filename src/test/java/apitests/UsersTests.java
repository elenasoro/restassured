package apitests;

import config.ApiConfig;
import models.UpdateUserRequest;
import models.UserDto;
import models.UserResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.*;

public class UsersTests {

    @Test
    public void getUserByIdTest() {

        UserDto expectedUser = UserDto.builder()
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
        UpdateUserRequest updateBody = UpdateUserRequest
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

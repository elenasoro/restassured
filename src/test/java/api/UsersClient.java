package api;

import config.ApiConfig;
import io.restassured.response.Response;
import models.UpdateUserRequest;

import static io.restassured.RestAssured.given;

public class UsersClient {

    public Response getUsers(int page) {
        return given()
                .spec(ApiConfig.requestSpec())
                .queryParam("page", page)
                .when()
                .get("/users");
    }

    public Response getUserById(String id) {
        return given()
                .spec(ApiConfig.requestSpec())
                .when()
                .get("/users/{id}", id);
    }

    public Response updateUser(String id, UpdateUserRequest body) {
        return given()
                .spec(ApiConfig.requestSpec())
                .body(body)
                .when()
                .patch("/users/{id}", id);
    }

    public Response deleteUser(String id) {
        return given()
                .spec(ApiConfig.requestSpec())
                .when()
                .delete("/users/{id}", id);
    }

    public Response getUsersWithDelay(int delay) {
        return given()
                .spec(ApiConfig.requestSpec())
                .queryParam("delay", delay)
                .when()
                .get("/users");
    }
}

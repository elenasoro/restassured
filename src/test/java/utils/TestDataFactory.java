package utils;

import models.UpdateUserRequest;
import models.UserDto;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static UserDto expectedUser() {
        return new UserDto(
                2,
                "janet.weaver@reqres.in",
                "Janet",
                "Weaver",
                "https://reqres.in/img/faces/2-image.jpg"
        );
    }

    public static UpdateUserRequest updateUserRequest() {
        return UpdateUserRequest.builder()
                .name("Lena")
                .job("QA")
                .build();
    }

    public static Object[][] invalidUserIds() {
        return new Object[][]{
                {"0"},
                {"-1"},
                {"abc"},
                {"2.5"}
        };
    }
}

package apitests;

import api.UsersClient;
import models.UpdateUserRequest;
import models.UserDto;
import models.UserResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.TestDataFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.*;

public class UsersTests {

    private final UsersClient usersClient = new UsersClient();

    @Test(description = "TC_001 — Проверка уникальности ID")
    public void getUsersWithUniqueIdsTest() {
        List<UserDto> users = usersClient.getUsers(1)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data", UserDto.class);

        assertThat(users).isNotEmpty();

        long uniqueIdsCount = users.stream()
                .map(UserDto::id)
                .distinct()
                .count();

        assertThat(uniqueIdsCount)
                .as("Количество уникальных ID должно совпадать с количеством пользователей")
                .isEqualTo((long) users.size());
    }

    @Test(description = "TC_002 — Фильтрация по email")
    public void filterUsersByEmailDomainTest() {
        List<UserDto> users = usersClient.getUsers(1)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data", UserDto.class);

        List<UserDto> filteredUsers = users.stream()
                .filter(user -> user.email() != null && user.email().endsWith("@reqres.in"))
                .toList();

        assertThat(filteredUsers)
                .as("Должны быть найдены пользователи с email в домене @reqres.in")
                .isNotEmpty();

        filteredUsers.forEach(System.out::println);

        boolean allEmailsMatch = filteredUsers.stream()
                .allMatch(user -> user.email().endsWith("@reqres.in"));

        assertThat(allEmailsMatch)
                .as("Email всех отобранных пользователей должен заканчиваться на @reqres.in")
                .isTrue();
    }

    @Test(description = "TC_003 — Проверка обязательных полей")
    public void usersShouldHaveRequiredFieldsTest() {
        List<UserDto> users = usersClient.getUsers(1)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data", UserDto.class);

        assertThat(users)
                .as("Список пользователей не должен быть пустым")
                .isNotEmpty();

        boolean allRequiredFieldsPresent = users.stream()
                .allMatch(user -> user.firstName() != null
                        && user.lastName() != null
                        && user.email() != null);

        assertThat(allRequiredFieldsPresent)
                .as("У всех пользователей firstName, lastName и email должны отличаться от null")
                .isTrue();

        boolean noRequiredFieldsBlank = users.stream()
                .noneMatch(user -> user.firstName().isBlank()
                        || user.lastName().isBlank()
                        || user.email().isBlank());

        assertThat(noRequiredFieldsBlank)
                .as("firstName, lastName и email не должны быть пустыми или состоять только из пробелов")
                .isTrue();
    }

    @Test(description = "TC_004 — Поиск пользователя")
    public void findUserByIdTest() {
        UserDto expectedUser = TestDataFactory.expectedUser();
        Integer targetUserId = expectedUser.id();

        List<UserDto> users = usersClient.getUsers(1)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data", UserDto.class);

        Optional<UserDto> foundUser = users.stream()
                .filter(user -> targetUserId.equals(user.id()))
                .findFirst();

        assertThat(foundUser)
                .as("Пользователь с ID %s должен присутствовать на странице 1", targetUserId)
                .isPresent();

        UserDto user = foundUser.orElseThrow();

        assertThat(user.firstName()).isEqualTo(expectedUser.firstName());
        assertThat(user.email()).isEqualTo(expectedUser.email());
    }

    @Test
    public void getUserByIdTest() {

        UserDto expectedUser = TestDataFactory.expectedUser();

        UserResponse response = usersClient.getUserById(expectedUser.id().toString())
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
        return TestDataFactory.invalidUserIds();
    }

    @Test(dataProvider = "invalidUserIds")
    public void getUserByInvalidIdTest(String id) {
        usersClient.getUserById(id)
                .then()
                .statusCode(404);
    }

    @Test
    public void updateUserTest() {
        UpdateUserRequest updateBody = TestDataFactory.updateUserRequest();

        usersClient.updateUser("2", updateBody)
                .then()
                .statusCode(200)
                .body("name", equalTo(updateBody.getName()))
                .body("job", equalTo(updateBody.getJob()));
    }

    @Test
    public void deleteUserTest() {
        usersClient.deleteUser("2")
                .then()
                .statusCode(204);
    }

    @Test
    public void delayResponseTest() {
        usersClient.getUsersWithDelay(3)
                .then()
                .statusCode(200);
    }


}

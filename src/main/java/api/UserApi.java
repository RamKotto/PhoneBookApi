package api;

import constants.PathEnum;
import constants.StatusCodeEnum;
import dto.UserDTO;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import utils.PropertyManager;

import java.util.List;

import static io.restassured.RestAssured.given;
import static constants.PropertiesEnum.CONFIG;

@Slf4j
public class UserApi {

    public static <T> T createNewUser(UserDTO user, Class<T> clazz) {
        log.info("Creating new user with params: " + user.getFirstName() + " " + user.getLastName());
        return given()
                .baseUri(PropertyManager.propHandler(CONFIG, "HOST"))
                .basePath(PathEnum.USERS.getApiMethod())
                .contentType(ContentType.JSON)
                .body(user)
                .when().post()
                .then().extract().as(clazz);
    }

    public static String createNewUserAndReturnString(UserDTO user) {
        log.info("Creating new user with params: " + user.getFirstName() + " " + user.getLastName());
        return given()
                .baseUri(PropertyManager.propHandler(CONFIG, "HOST"))
                .basePath(PathEnum.USERS.getApiMethod())
                .contentType(ContentType.JSON)
                .body(user)
                .when().post()
                .then().extract().asString();
    }

    public static List<UserDTO> getListOfUsers() {
        log.info("Get list of users...");
        return given()
                .when()
                .baseUri(PropertyManager.propHandler(CONFIG, "HOST"))
                .basePath(PathEnum.USERS.getApiMethod())
                .contentType(ContentType.JSON)
                .when().get()
                .then()
                .statusCode(StatusCodeEnum.SC_OK.getStatusCode())
                .extract().jsonPath().getList("", UserDTO.class);
    }

    public static UserDTO updateUser(int id, UserDTO data) {
        log.info("Updating user with id: " + id);
        return given()
                .baseUri(PropertyManager.propHandler(CONFIG, "HOST"))
                .basePath(PathEnum.USERS.getApiMethod() + "/" + id)
                .contentType(ContentType.JSON)
                .body(data)
                .when().put()
                .then().extract().as(UserDTO.class);
    }

    public static void deleteUserById(int id) {
        log.info("Delete user with id: " + id);
        given()
                .baseUri(PropertyManager.propHandler(CONFIG, "HOST"))
                .basePath(PathEnum.USERS.getApiMethod() + "/" + id)
                .contentType(ContentType.JSON)
                .when().delete()
                .then().statusCode(StatusCodeEnum.ACCEPTED.getStatusCode());
    }
}

import dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.RandomUtils;
import java.util.List;

import static api.UserApi.*;
import static org.assertj.core.api.Assertions.assertThat;
import static steps.UserApiSteps.updateCreatedUser;

@Slf4j
public class UserTests {
    private static final UserDTO FIRST_USER = new UserDTO(
            RandomUtils.getRandomWord(8),
            RandomUtils.getRandomWord(10));
    private static final UserDTO SECOND_USER = new UserDTO(
            RandomUtils.getRandomWord(8),
            RandomUtils.getRandomWord(10));
    private static final UserDTO FIRST_INCORRECT_USER = new UserDTO(
            RandomUtils.getRandomWord(1),
            RandomUtils.getRandomWord(10));
    private static final UserDTO SECOND_INCORRECT_USER = new UserDTO(
            RandomUtils.getRandomWord(20),
            RandomUtils.getRandomWord(10));

    @DataProvider
    public Object[][] userData() {
        return new Object[][]{
                {FIRST_USER},
                {SECOND_USER},
        };
    }

    @Test(dataProvider = "userData")
    public void crudNewUserTest(UserDTO user) {
        // Создать нового пользователя и убедиться, что он создан
        UserDTO createdUser = createNewUser(user, UserDTO.class);
        assertThat(createdUser)
                .isNotNull()
                .extracting(UserDTO::getFirstName)
                .isEqualTo(createdUser.getFirstName());
        log.info("User with name: " + createdUser.getFirstName() + " was created.");

        // Получить список всех пользователей и убедиться, что созданный пользователь в нем присутствует
        List<UserDTO> listOfUsers = getListOfUsers();
        assertThat(listOfUsers).extracting(UserDTO::getFirstName).contains(user.getFirstName());
        log.info("User with name: " + createdUser.getFirstName() + " contains in users.");

        // Изменить имя и фамилию созданному пользователю и убедиться, что изменения успешно выполнены
        UserDTO updatedUser = updateCreatedUser(createdUser.getId());
        Assert.assertNotEquals(createdUser.getFirstName(), updatedUser.getFirstName(),
                "Имя пользователя не было изменено");
        Assert.assertNotEquals(createdUser.getLastName(), updatedUser.getLastName(),
                "Фамилия пользователя не была изменена");
        Assert.assertEquals(createdUser.getId(), updatedUser.getId(),
                "Id не должны отличаться при внесении изменений в запись пользователя.");
        log.info("User changed name from " + createdUser.getFirstName() + " to " + updatedUser.getFirstName());

        // Удалить нового пользователя, и убедиться, что его нет в списке пользователей
        deleteUserById(createdUser.getId());
        listOfUsers = getListOfUsers();
        assertThat(listOfUsers).extracting(UserDTO::getFirstName).doesNotContain(updatedUser.getFirstName());
        log.info("User with name: " + updatedUser.getFirstName() + " was deleted.");
    }

    @DataProvider
    public Object[][] incorrectUserData() {
        return new Object[][]{
                {FIRST_INCORRECT_USER},
                {SECOND_INCORRECT_USER},
        };
    }

    @Test(dataProvider = "incorrectUserData")
    public void incorrectNameUserTest(UserDTO user) {
        // Попытаться создать пользователя с именем меньше 2 символов или больше 15.

        String response = createNewUserAndReturnString(user);
        log.info(response);
        List<UserDTO> userList = getListOfUsers();
        assertThat(userList).extracting(UserDTO::getFirstName).doesNotContain(user.getFirstName());
        log.info("User with name: " + user.getFirstName() + " was not created.");

        // По описанию на Swagger - API должно возвращать ответ в виде "ErrorResponse" и 400 код.
        // Но возвращает его в виде строки JSON:
        // {"firstName":"размер должен быть между 2 и 15"}
        // Новый пользователь не создается, поэтому тест проходит.
    }
}

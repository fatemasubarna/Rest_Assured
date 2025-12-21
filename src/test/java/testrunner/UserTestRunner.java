package testrunner;

import com.github.javafaker.Faker;
import config.Setup;
import config.UserModel;
import controller.UserController;
import io.restassured.response.Response;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Utils;
import java.io.IOException;

public class UserTestRunner extends Setup {
    UserController userController;
    Faker faker = new Faker();

    @BeforeClass
    public void myUserController() {
        userController = new UserController(prop);
    }

    @Test(priority = 1, description = "Admin login")
    public void adminLogin() throws ConfigurationException {
        UserModel userModel = new UserModel();
        userModel.setEmail("admin@test.com");
        userModel.setPassword("admin123");
        Response res = userController.adminLogin(userModel);
        Utils.setEnv("token", res.jsonPath().get("token"));
        Assert.assertEquals(res.getStatusCode(), 200);
    }

    @Test(priority = 2, description = "Get User list", dependsOnMethods = "adminLogin")
    public void getUser() throws IOException {
        Response res = userController.getUser();
        Assert.assertEquals(res.getStatusCode(), 200);
    }

    @Test(priority = 3, description = "Create new user")
    public void registerUser() throws ConfigurationException {
        UserModel userModel = new UserModel();
        String password = "Pa$$w0rd!";
        String email = "user" + System.currentTimeMillis() + "@test.com";

        userModel.setFirstName(faker.name().firstName());
        userModel.setLastName(faker.name().lastName());
        userModel.setEmail(email);
        userModel.setPassword(password);
        userModel.setPhoneNumber("+1202555" + Utils.generateRandomNumber(1000, 9999));
        userModel.setAddress(faker.address().fullAddress());
        userModel.setGender("Male");
        userModel.setTermsAccepted(true);

        Response res = userController.registerUser(userModel);
        Assert.assertEquals(res.getStatusCode(), 201);

        Utils.setEnv("userId", res.jsonPath().get("user._id"));
        Utils.setEnv("email", email);
        Utils.setEnv("password", password);
    }

    @Test(priority = 4, description = "Search User by id", dependsOnMethods = "registerUser")
    public void searchUser() throws IOException, ConfigurationException {
        Response res = userController.searchUser(Utils.getEnv("userId"));
        Assert.assertEquals(res.getStatusCode(), 200);
    }

    @Test(priority = 5, description = "Edit User", dependsOnMethods = "registerUser")
    public void editUser() throws ConfigurationException {
        UserModel userModel = new UserModel();
        userModel.setFirstName("UpdatedName");
        userModel.setLastName("UpdatedLast");
        userModel.setEmail(Utils.getEnv("email")); // Mandatory to avoid 500
        userModel.setPhoneNumber("+12025550000");
        userModel.setAddress("123 Updated St");

        Response res = userController.editUser(Utils.getEnv("userId"), userModel);
        Assert.assertEquals(res.getStatusCode(), 200);
    }

    @Test(priority = 6, description = "User login", dependsOnMethods = "registerUser")
    public void userLogin() throws ConfigurationException {
        UserModel userModel = new UserModel();
        userModel.setEmail(Utils.getEnv("email"));
        userModel.setPassword(Utils.getEnv("password"));
        Response res = userController.userLogin(userModel);

        Assert.assertEquals(res.getStatusCode(), 200);
        Utils.setEnv("token", res.jsonPath().get("token"));
    }

    @Test(priority = 7, description = "Create new item", dependsOnMethods = "userLogin")
    public void createItem() throws ConfigurationException {
        UserModel costModel = new UserModel();
        costModel.setItemName(faker.commerce().productName());
        costModel.setQuantity(2);
        costModel.setAmount(100);
        costModel.setPurchaseDate("2025-12-21T00:00:00.000Z");
        costModel.setMonth("December");
        costModel.setRemarks("Automation Test");

        Response res = userController.createItem(costModel);
        Assert.assertEquals(res.getStatusCode(), 201);
        Utils.setEnv("itemId", res.jsonPath().get("item._id"));
    }

    @Test(priority = 8, description = "Get Item list", dependsOnMethods = "userLogin")
    public void getItem() {
        Response res = userController.getItem();
        Assert.assertEquals(res.getStatusCode(), 200);
    }

    @Test(priority = 9, description = "Update Item", dependsOnMethods = "createItem")
    public void updateItem() throws ConfigurationException {
        UserModel costModel = new UserModel();
        costModel.setItemName("Updated Item Name");
        costModel.setQuantity(5);
        costModel.setAmount(500);
        costModel.setPurchaseDate("2025-12-21T00:00:00.000Z");
        costModel.setMonth("December");
        costModel.setRemarks("Updated via script");

        Response res = userController.updateItem(Utils.getEnv("itemId"), costModel);
        Assert.assertEquals(res.getStatusCode(), 200);
    }

    @Test(priority = 10, description = "Delete Item", dependsOnMethods = "updateItem")
    public void deleteCost() throws ConfigurationException {
        Response res = userController.deleteItem(Utils.getEnv("itemId"));
        Assert.assertEquals(res.getStatusCode(), 200);
    }
}
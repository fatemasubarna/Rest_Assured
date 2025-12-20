package testrunner;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.javafaker.Faker;
import config.Setup;
import config.UserModel;
import controller.UserController;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Utils;


import java.io.IOException;

import static org.yaml.snakeyaml.tokens.Token.ID.Value;

public class UserTestRunner extends Setup {
    UserController userController;

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

        JsonPath jsonObj = res.jsonPath();
        String token = jsonObj.get("token");
        Utils.setEnv("token", token);

        System.out.println(token);
    }

    @Test(priority = 2, description = "Create new user")
    public void registerUser() throws ConfigurationException {
        UserModel userModel = new UserModel();
        Faker faker = new Faker();
        userModel.setName(faker.name().firstName());
        userModel.setName(faker.name().lastName());
        userModel.setEmail(faker.internet().emailAddress().toString());
        userModel.setPassword("12345678");
        userModel.setPhone_number("0120" + Utils.generateRandomNumber(1000000, 9999999));
        userModel.setGender("Male");
        userModel.setTerms_and_condition("True");

        Response res = userController.registerUser(userModel);

        JsonPath jsonObj = res.jsonPath();

        int userId = jsonObj.get("user.id");
        String name = jsonObj.get("user.name");
        String email = jsonObj.get("user.email");
        String password = jsonObj.get("user.password");
        String phoneNumber = jsonObj.get("user.phone_number");
        String gender = jsonObj.get("user.gender");



        Utils.setEnv("userId", String.valueOf(userId));
        Utils.setEnv("name", name);
        Utils.setEnv("email", email);
        Utils.setEnv("password", password);
        Utils.setEnv("phoneNumber", phoneNumber);
        Utils.setEnv("gender",gender);

        System.out.println(res.asString());

        //Assert.assertEquals(jsonObj.get("message"), "User created");
    }

    @Test(priority = 3, description = "Get User list")
    public void getUser() throws IOException {
        Response res = userController.getUser(prop.getProperty("userId"));
        System.out.println(res.asString());

        JsonPath jsonObj = res.jsonPath();
    }


    @Test(priority = 4, description = "Search User by id")
    public void searchUser() throws IOException {
        Response res = userController.searchUser(prop.getProperty("userId"));
        System.out.println(res.asString());

        JsonPath jsonObj = res.jsonPath();
    }

    @Test(priority = 5, description = "Edit User")
    public void editUser() throws IOException {
        Response res = userController.editUser(prop.getProperty("userId"));
        System.out.println(res.asString());

        JsonPath jsonObj = res.jsonPath();
        Assert.assertEquals(jsonObj.get("message"), "User updated successfully!");
    }

    @Test(priority = 6, description = "User login")
    public void userLogin() throws ConfigurationException {
        UserModel userModel = new UserModel();
        userModel.setEmail("Campos@gmail.com");
        userModel.setPassword("12345678");
        Response res = userController.userLogin(userModel);

        JsonPath jsonObj = res.jsonPath();
        String token = jsonObj.get("token");
        Utils.setEnv("token", token);

        System.out.println(token);
    }

    @Test(priority = 7, description = "Create new item")
    public void createItem() throws ConfigurationException {
        UserModel costModel = new UserModel();
        Faker faker = new Faker();
        costModel.setItemName(faker.name().name());
        costModel.setQuantity(faker.number().numberBetween(1, 100));
        costModel.setAmount(faker.number().numberBetween(1, 1000));
        costModel.setPurchaseDate("2025-12-17T00:00:00.000Z");
        costModel.setMonth("December");
        costModel.setRemarks("Its a good item.");

        Response res = userController.createItem(costModel);

        JsonPath jsonObj = res.jsonPath();

        int itemId = jsonObj.get("item.id");
        String name = jsonObj.get("item.itemname");
        int quantity = jsonObj.get("item.quantity");
        int amount = jsonObj.get("item.amount");
        String purchaseDate = jsonObj.get("item.purchaseDate");
        String month = jsonObj.get("item.month");



        Utils.setEnv("itemId", String.valueOf(itemId));
        Utils.setEnv("name", name);
        Utils.setEnv("amount", String.valueOf(amount));
        Utils.setEnv("quantity", String.valueOf(quantity));
        Utils.setEnv("purchaseDate", purchaseDate);
        Utils.setEnv("month",month);

        System.out.println(res.asString());

        //Assert.assertEquals(jsonObj.get("message"), "User created");
    }

    @Test(priority = 8, description = "Get Item list")
    public void getItem() throws IOException {
        Response res = userController.getItem();
        System.out.println(res.asString());

        JsonPath jsonObj = res.jsonPath();
    }


    @Test(priority = 9, description = "Update Item")
    public void updateItem() throws IOException {
        UserModel costModel = new UserModel();
        Faker faker = new Faker();

        costModel.setItemName(faker.commerce().productName());
        costModel.setQuantity(faker.number().numberBetween(1, 50));
        costModel.setAmount(faker.number().numberBetween(100, 2000));

        Response res = userController.updateItem(
                Utils.getEnv("itemId"), costModel
        );

        System.out.println(res.asString());

        JsonPath jsonObj = res.jsonPath();
        Assert.assertEquals(res.getStatusCode(), 200);
        Assert.assertEquals(
                jsonObj.get("message"),
                "Cost details updated successfully."
        );


    }

    @Test(priority = 10, description = "Delete Item")
    public void deleteCost() throws IOException {

        // Debug check
        System.out.println("ITEM ID = " + Utils.getEnv("itemId"));

        Response res = userController.deleteCost(
                Utils.getEnv("itemId")
        );

        System.out.println(res.asString());

        JsonPath jsonObj = res.jsonPath();

        Assert.assertEquals(res.getStatusCode(), 200);
        Assert.assertEquals(
                jsonObj.get("message"),
                "Cost deleted successfully."
        );
    }






}

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

public class UserTestRunner extends Setup {

    private UserController userController;
    private Faker faker = new Faker();

    @BeforeClass
    public void setupController() {
        userController = new UserController(prop);
    }

    @Test(priority = 1, description = "Admin login")
    public void adminLogin() {
        UserModel admin = new UserModel();
        admin.setEmail(prop.getProperty("adminEmail", "admin@test.com"));
        admin.setPassword(prop.getProperty("adminPassword", "admin123"));

        Response res = userController.adminLogin(admin);
        Assert.assertEquals(res.getStatusCode(), 200);
        Utils.setEnv("adminToken", res.jsonPath().getString("token"));
    }

    @Test(priority = 2, description = "Get User list", dependsOnMethods = "adminLogin")
    public void getUserList() {
        Response res = userController.getUser(Utils.getEnv("adminToken"));
        Assert.assertEquals(res.getStatusCode(), 200);
    }

    @Test(priority = 3, description = "Register new user", dependsOnMethods = "adminLogin")
    public void registerUser() {
        UserModel user = new UserModel();
        String email = "user" + System.currentTimeMillis() + "@test.com";
        String password = "Pa$$w0rd!";

        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber("+1202555" + Utils.generateRandomNumber(1000, 9999));
        user.setAddress(faker.address().fullAddress());
        user.setGender("Male");
        user.setTermsAccepted(true);

        Response res = userController.registerUser(user, Utils.getEnv("adminToken"));
        Assert.assertEquals(res.getStatusCode(), 201);

        String userId = res.jsonPath().getString("user._id");
        if (userId == null) userId = res.jsonPath().getString("_id");
        Assert.assertNotNull(userId, "User ID must not be null");

        Utils.setEnv("userId", userId);

        //Utils.setEnv("userId", res.jsonPath().getString("user._id"));
        Utils.setEnv("email", email);
        Utils.setEnv("password", password);
    }

    @Test(priority = 4, description = "Update User", dependsOnMethods = "registerUser")
    public void editUser() {


        String userId = Utils.getEnv("userId");
        Assert.assertNotNull(userId, "User ID missing in env");


        UserModel user = new UserModel();
        user.setFirstName("UpdatedName");
        user.setLastName("UpdatedLast");
        user.setPhoneNumber("+12025550000");
        user.setAddress("123 Updated Street");
        user.setGender("Male");
        Response res = userController.editUser(
                userId,
                user,
                Utils.getEnv("adminToken") // ✅ Admin token required
        );

        System.out.println("Update User Response:");
        res.prettyPrint();

        Assert.assertEquals(res.getStatusCode(), 200, "User update failed");

    }


    @Test(priority = 5, description = "User login", dependsOnMethods = "registerUser")
    public void userLogin() {
        UserModel user = new UserModel();
        user.setEmail(Utils.getEnv("email"));
        user.setPassword(Utils.getEnv("password"));

        Response res = userController.userLogin(user);
        Assert.assertEquals(res.getStatusCode(), 200);
        Utils.setEnv("userToken", res.jsonPath().getString("token"));
    }

    // ---------------- ITEM / COST ----------------
    @Test(priority = 6, description = "Create new item", dependsOnMethods = "userLogin")
    public void createItem() {
        UserModel item = new UserModel();
        item.setItemName(faker.commerce().productName());
        item.setQuantity(2);
        item.setAmount(100);
        item.setPurchaseDate("2025-12-21T00:00:00.000Z");
        item.setMonth("December");
        item.setRemarks("Automation Test");

        Response res = userController.createItem(item, Utils.getEnv("userToken"));
        Assert.assertEquals(res.getStatusCode(), 201);

        String itemId = res.jsonPath().getString("cost._id");
        if (itemId == null) itemId = res.jsonPath().getString("_id");
        Assert.assertNotNull(itemId, "Item ID must not be null");

        Utils.setEnv("itemId", itemId);
    }

    @Test(priority = 7, description = "Get Item list", dependsOnMethods = "userLogin")
    public void getItemList() {
        Response res = userController.getItem(Utils.getEnv("userToken"));
        Assert.assertEquals(res.getStatusCode(), 200);
    }

    @Test(priority = 8, description = "Update Item", dependsOnMethods = "createItem")
    public void updateItem() {
        UserModel item = new UserModel();
        item.setItemName("new item updated");
        item.setQuantity(5);
        item.setAmount(500);
        item.setPurchaseDate("2025-12-21T00:00:00.000Z");
        item.setMonth("December");
        item.setRemarks("Updated the product");

        Response res = userController.updateItem(Utils.getEnv("itemId"), item, Utils.getEnv("userToken"));
        Assert.assertEquals(res.getStatusCode(), 200);
    }

    @Test(priority = 9, description = "Delete Item", dependsOnMethods = "updateItem")
    public void deleteItem() {
        Response res = userController.deleteItem(Utils.getEnv("itemId"), Utils.getEnv("userToken"));
        Assert.assertEquals(res.getStatusCode(), 200);
    }
}

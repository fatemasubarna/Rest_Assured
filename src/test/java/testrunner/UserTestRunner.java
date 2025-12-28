package testrunner;

import com.github.javafaker.Faker;
import config.Setup;
import config.UserModel;
import controller.UserController;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Utils;

public class UserTestRunner extends Setup {

    private UserController userController;
    private Faker faker = new Faker();

    // Store original user data for update tests
    private String originalFirstName;
    private String originalLastName;
    private String originalAddress;
    private String originalPhoneNumber;
    private String originalGender;

    @BeforeClass
    public void setupController() {
        userController = new UserController(prop);
    }

    // ============================================================
    // ADMIN LOGIN
    // ============================================================

    @Test(priority = 1, description = "Admin login")
    public void adminLogin() {
        UserModel admin = new UserModel();
        admin.setEmail(prop.getProperty("adminEmail", "admin@test.com"));
        admin.setPassword(prop.getProperty("adminPassword", "admin123"));

        Response res = userController.adminLogin(admin);
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 200, "Admin login should return 200");

        String token = res.jsonPath().getString("token");
        Assert.assertNotNull(token, "Admin token should not be null");

        Utils.setEnv("adminToken", token);
        System.out.println("✓ Admin logged in successfully");
    }

    // ============================================================
    // USER LIST
    // ============================================================

    @Test(priority = 2, description = "Get user list", dependsOnMethods = "adminLogin")
    public void getUserList() {
        Response res = userController.getUser(Utils.getEnv("adminToken"));
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 200, "Get user list should return 200");
        System.out.println("✓ User list retrieved successfully");
    }

    // ============================================================
    // REGISTER USER
    // ============================================================

    @Test(priority = 3, description = "Register new user", dependsOnMethods = "adminLogin")
    public void registerUser() {
        UserModel user = new UserModel();
        String email = "user" + System.currentTimeMillis() + "@test.com";
        String password = "Pa$$w0rd!";

        // Store original values for update test
        originalFirstName = faker.name().firstName();
        originalLastName = faker.name().lastName();
        originalPhoneNumber = "+1202555" + Utils.generateRandomNumber(1000, 9999);
        originalAddress = faker.address().fullAddress();
        originalGender = "Male";

        user.setFirstName(originalFirstName);
        user.setLastName(originalLastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(originalPhoneNumber);
        user.setAddress(originalAddress);
        user.setGender(originalGender);
        user.setTermsAccepted(true);

        Response res = userController.registerUser(user, Utils.getEnv("adminToken"));
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 201, "User registration should return 201");

        // Handle different response structures
        String userId = res.jsonPath().getString("user._id");
        if (userId == null) userId = res.jsonPath().getString("_id");
        Assert.assertNotNull(userId, "User ID must not be null");

        Utils.setEnv("userId", userId);
        Utils.setEnv("email", email);
        Utils.setEnv("password", password);

        System.out.println("✓ User registered successfully with ID: " + userId);
    }

    // ============================================================
    // USER LOGIN
    // ============================================================

    @Test(priority = 4, description = "User login", dependsOnMethods = "registerUser")
    public void userLogin() {
        UserModel user = new UserModel();
        user.setEmail(Utils.getEnv("email"));
        user.setPassword(Utils.getEnv("password"));

        Response res = userController.userLogin(user);
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 200, "User login should return 200");

        String token = res.jsonPath().getString("token");
        Assert.assertNotNull(token, "User token should not be null");

        Utils.setEnv("userToken", token);
        System.out.println("✓ User logged in successfully");
    }

    // ============================================================
    // UPDATE USER
    // ============================================================

    @Test(priority = 5, description = "Update user", dependsOnMethods = "userLogin")
    public void updateUser() {
        UserModel user = new UserModel();

        // Fields to update
        String updatedFirstName = "UpdatedFirstName";
        String updatedPhoneNumber = "+12025559999";

        user.setFirstName(updatedFirstName);
        user.setPhoneNumber(updatedPhoneNumber);

        // Use original values from registration
        user.setLastName(originalLastName);
        user.setAddress(originalAddress);
        user.setGender(originalGender);
        user.setTermsAccepted(true);
        user.setEmail(Utils.getEnv("email"));
        user.setPassword(Utils.getEnv("password")); // Send the password back

        Response res = userController.editUser(
                Utils.getEnv("userId"),
                user,
                Utils.getEnv("adminToken")
        );

        res.then().log().ifValidationFails();
        Assert.assertEquals(res.getStatusCode(), 200, "Update should return 200");

        // Verify updated fields
        Assert.assertEquals(res.jsonPath().getString("firstName"), updatedFirstName,
                "First name should be updated");
        Assert.assertEquals(res.jsonPath().getString("phoneNumber"), updatedPhoneNumber,
                "Phone number should be updated");

        System.out.println("✓ User updated successfully");
        System.out.println("⚠️  Note: Password was re-hashed during update. User cannot log in again with original password.");
    }

    // ============================================================
    // ITEM / COST MANAGEMENT
    // ============================================================

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
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 201, "Create item should return 201");

        String itemId = res.jsonPath().getString("cost._id");
        if (itemId == null) itemId = res.jsonPath().getString("_id");
        Assert.assertNotNull(itemId, "Item ID must not be null");

        Utils.setEnv("itemId", itemId);
        System.out.println("Item created successfully with ID: " + itemId);
    }

    @Test(priority = 7, description = "Get item list", dependsOnMethods = "userLogin")
    public void getItemList() {
        Response res = userController.getItem(Utils.getEnv("userToken"));
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 200, "Get item list should return 200");
        System.out.println("Item list retrieved successfully");
    }

    @Test(priority = 8, description = "Update item", dependsOnMethods = "createItem")
    public void updateItem() {
        UserModel item = new UserModel();
        item.setItemName("Updated Product Name");
        item.setQuantity(5);
        item.setAmount(500);
        item.setPurchaseDate("2025-12-21T00:00:00.000Z");
        item.setMonth("December");
        item.setRemarks("Updated the product");

        Response res = userController.updateItem(Utils.getEnv("itemId"), item, Utils.getEnv("userToken"));
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 200, "Update item should return 200");
        System.out.println("Item updated successfully");
    }

    @Test(priority = 9, description = "Delete item", dependsOnMethods = "updateItem")
    public void deleteItem() {
        Response res = userController.deleteItem(Utils.getEnv("itemId"), Utils.getEnv("userToken"));
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 200, "Delete item should return 200");
        System.out.println( "Item deleted successfully");
    }
}
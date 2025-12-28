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


    // --------ADMIN LOGIN-----

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
        System.out.println("Admin logged in successfully");
    }

    @Test(priority = 1, description = "Admin login with invalid credentials")
    public void adminLoginWithInvalidCredentials() {

        UserModel admin = new UserModel();
        admin.setEmail("wrongadmin@test.com");
        admin.setPassword("wrongpassword");

        Response res = userController.adminLogin(admin);
        res.then().log().ifValidationFails();

        // Validate status code (usually 401 or 400 depending on API)
        Assert.assertTrue(
                res.getStatusCode() == 401 || res.getStatusCode() == 400,
                "Invalid login should return 400 or 401"
        );

        // Validate error message
        String message = res.jsonPath().getString("message");
        Assert.assertNotNull(message, "Invalid email or password");
    }

    @Test(priority = 1, description = "Admin login without credentials")
    public void adminLoginWithoutCredentials() {

        UserModel admin = new UserModel(); // no email, no password

        Response res = userController.adminLogin(admin);
        res.then().log().ifValidationFails();

        // Validate status code
        Assert.assertEquals(res.getStatusCode(), 401, "Missing credentials should return 400");

        // Validate error message
        String message = res.jsonPath().getString("message");
        Assert.assertNotNull(message, "Invalid email or password");
    }


    // --------------- Get User List ----------------

    @Test(priority = 2, description = "Get user list", dependsOnMethods = "adminLogin")
    public void getUserList() {
        Response res = userController.getUser(Utils.getEnv("adminToken"));
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 200, "Get user list should return 200");
    }


    // ---------------- REGISTER NEW USER ----------------
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

        System.out.println("User registered successfully with ID: " + userId);
    }

    @Test(priority = 3, description = "Register user with existing email", dependsOnMethods = "registerUser")
    public void registerUserWithExistingEmail() {

        UserModel user = new UserModel();
        user.setFirstName("existing");
        user.setLastName("User");
        user.setEmail(Utils.getEnv("email"));
        user.setPassword("Pa$$w0rd!");
        user.setPhoneNumber("+12025558888");
        user.setAddress("Dhaka");
        user.setGender("Male");
        user.setTermsAccepted(true);

        Response res = userController.registerUser(user, Utils.getEnv("adminToken"));
        res.then().log().ifValidationFails();

        Assert.assertTrue(
                res.getStatusCode() == 400 || res.getStatusCode() == 409,
                "User already exists with this email address"
        );
    }




// -------------SEARCH BY USER ID--------------

    @Test(priority = 4, description = "Search user by ID", dependsOnMethods = "registerUser")
    public void searchUserById() {
        String userId = Utils.getEnv("userId");

        Response res = userController.searchUser(userId, Utils.getEnv("adminToken"));
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 200, "Search user should return 200");

        // Verify the returned user matches
        Assert.assertEquals(res.jsonPath().getString("_id"), userId, "User ID should match");
        Assert.assertEquals(res.jsonPath().getString("email"), Utils.getEnv("email"), "Email should match");
        Assert.assertEquals(res.jsonPath().getString("firstName"), originalFirstName, "First name should match");
        Assert.assertEquals(res.jsonPath().getString("lastName"), originalLastName, "Last name should match");

        System.out.println("✓ User found successfully by ID: " + userId);
    }


    // -------------USER LOGIN-------------

    @Test(priority = 5, description = "User login", dependsOnMethods = "registerUser")
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
        System.out.println("User logged in successfully");
    }


    @Test(priority = 5, description = "User login without credentials", dependsOnMethods = "adminLogin")
    public void userLoginWithoutCredentials() {
        UserModel user = new UserModel(); // empty email & password

        Response res = userController.userLogin(user);
        res.then().log().all();

        // Backend may return 400 or 401, not JSON
        int statusCode = res.getStatusCode();
        Assert.assertTrue(
                res.getStatusCode() == 401 || res.getStatusCode() == 409,
                "Invalid email or password"
        );
    }
    @Test(priority = 5, description = "User login with wrong email", dependsOnMethods = "registerUser")
    public void userLoginWrongEmail() {
        UserModel user = new UserModel();
        user.setEmail("wrongemail@test.com"); // invalid email
        user.setPassword(Utils.getEnv("password")); // correct password

        Response res = userController.userLogin(user);
        res.then().log().all();

        int statusCode = res.getStatusCode();
        Assert.assertTrue(res.getStatusCode() == 401 || res.getStatusCode() == 409,
                "Invalid email or password");
    }
    


    // UPDATE USER

    @Test(priority = 6, description = "Update user", dependsOnMethods = "userLogin")
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

        System.out.println("User updated successfully");
        System.out.println("Note: Password was re-hashed during update. User cannot log in again with original password.");
    }


    // CREATE ITEM


    @Test(priority = 7, description = "Create new item", dependsOnMethods = "userLogin")
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

    // GET ITEM LIST

    @Test(priority = 8, description = "Get item list", dependsOnMethods = "userLogin")
    public void getItemList() {
        Response res = userController.getItem(Utils.getEnv("userToken"));
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 200, "Get item list should return 200");
        System.out.println("Item list retrieved successfully");
    }

    // UPDATE ITEM

    @Test(priority = 9, description = "Update item", dependsOnMethods = "createItem")
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

    // DELETE ITEM

    @Test(priority = 10, description = "Delete item", dependsOnMethods = "updateItem")
    public void deleteItem() {
        Response res = userController.deleteItem(Utils.getEnv("itemId"), Utils.getEnv("userToken"));
        res.then().log().ifValidationFails();

        Assert.assertEquals(res.getStatusCode(), 200, "Delete item should return 200");
        System.out.println( "Item deleted successfully");
    }
}
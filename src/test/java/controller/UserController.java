package controller;

import config.UserModel;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.Properties;
import static io.restassured.RestAssured.given;

public class UserController {

    private final Properties prop;

    public UserController(Properties prop) {
        RestAssured.baseURI = prop.getProperty("baseURL");
        this.prop = prop;
    }

    // ---------------- AUTH ----------------
    public Response adminLogin(UserModel userModel) {
        return given()
                .contentType("application/json")
                .body(userModel)
                .when()
                .post("/api/auth/login");
    }

    public Response userLogin(UserModel userModel) {
        return given()
                .contentType("application/json")
                .body(userModel)
                .when()
                .post("/api/auth/login");
    }

    public Response registerUser(UserModel userModel, String token) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(userModel)
                .when()
                .post("/api/auth/register");
    }

    // ---------------- USER ----------------
    public Response getUser(String token) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/user/users");
    }

    public Response searchUser(String userId, String adminToken) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/user/users/" + userId);
    }


    public Response editUser(String userId, UserModel user, String token) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(user)
                .when()
                .put("/api/user/users/" + userId);
    }


    // ---------------- ITEM / COST ----------------
    public Response getItem(String token) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/costs");
    }

    public Response createItem(UserModel costModel, String token) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(costModel)
                .when()
                .post("/api/costs");
    }

    public Response updateItem(String itemId, UserModel costModel, String token) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(costModel)
                .when()
                .put("/api/costs/" + itemId);
    }

    public Response deleteItem(String itemId, String token) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/costs/" + itemId);
    }
}

package controller;

import config.UserModel;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.io.IOException;
import java.util.Properties;
import static io.restassured.RestAssured.given;

public class UserController {
    Properties prop;

    public UserController(Properties prop) {
        RestAssured.baseURI = "https://dailyfinanceapi.roadtocareer.net";
        this.prop = prop;
    }

    public Response registerUser(UserModel userModel) {
        return given().contentType("application/json").body(userModel).when().post("/api/auth/register");
    }

    public Response adminLogin(UserModel userModel) {
        return given().contentType("application/json").body(userModel).when().post("/api/auth/login");
    }

    public Response getUser() throws IOException {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("token"))
                .when().get("/api/user/users");
    }

    public Response searchUser(String userId) throws IOException {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("token"))
                .when()
                .get("/api/user/users/" + userId); // Fixed: Removed /search/
    }

    public Response editUser(String userId, UserModel userModel) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("token"))
                .body(userModel)
                .when().put("/api/user/update/" + userId); // Keep /update/ for User Edit
    }

    public Response userLogin(UserModel userModel) {
        return given().contentType("application/json").body(userModel).when().post("/api/auth/login");
    }

    public Response getItem() {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("token"))
                .when().get("/api/costs");
    }

    public Response createItem(UserModel costModel) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("token"))
                .body(costModel)
                .when().post("/api/costs");
    }

    public Response updateItem(String itemId, UserModel costModel) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("token"))
                .body(costModel)
                .when().put("/api/costs/" + itemId); // Fixed: Removed /update/
    }

    public Response deleteItem(String itemId) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("token"))
                .when().delete("/api/costs/" + itemId);
    }
}
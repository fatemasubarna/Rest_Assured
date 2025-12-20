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
        RestAssured.baseURI = "https://dailyfinance.roadtocareer.net";
        this.prop = prop;
    }

    public Response registerUser(UserModel userModel) {
        Response res = given().contentType("application/json")
                .header("Authorization", "bearer " + prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", prop.getProperty("partnerKey"))
                .body(userModel)
                .when().post("/api/auth/login");
        return res;
    }

    public Response adminLogin(UserModel userModel) {
        Response res = given().contentType("application/json")
                .body(userModel).when()
                .post("/api/auth/login");
        return res;
    }

    public Response getUser(String userId) throws IOException {
        Response res = given().contentType("application/json")
                .header("Authorization", "bearer " + prop.getProperty("token"))
                .when().get("api/user/users");
        return res;
    }


    public Response searchUser(String userId) throws IOException {
        Response res = given().contentType("application/json")
                .header("Authorization", "bearer " + prop.getProperty("token"))
                .when().get("api/user/users" + userId);
        return res;
    }

    public Response editUser(String userId) {
        Response res = given().contentType("application/json")
                .header("Authorization", "bearer " + prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", prop.getProperty("partnerKey"))
                .when().put("/api/user/" + userId);
        return res;
    }

    public Response userLogin(UserModel userModel) {
        Response res = given().contentType("application/json")
                .body(userModel).when()
                .post("/api/auth/login");
        return res;
    }

    public Response getItem() {
        Response res = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("userLogintoken"))
                .header("X-AUTH-SECRET-KEY", prop.getProperty("partnerKey"))
                //.body(costModel)
                .when()
                .get("/api/costs");
        return res;
    }


    public Response createItem(UserModel costModel) {
        Response res = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", prop.getProperty("partnerKey"))
                .body(costModel)
                .when()
                .post("/api/costs");
        return res;

    }

    public Response searchById(String costId) {
        Response res = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("token"))
                .when()
                .get("/api/costs/" + costId);
        return res;
    }



    public Response updateItem(String itemId, UserModel costModel) {
        Response res = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", prop.getProperty("partnerKey"))
                .body(costModel)
                .when()
                .put("/api/costs/" + itemId);
        return res;
    }

    public Response deleteCost(String costId) {
        Response res = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", prop.getProperty("partnerKey"))
                //.body(costModel)
                .when()
                .delete("/api/costs/" + costId);
        return res;
    }

}

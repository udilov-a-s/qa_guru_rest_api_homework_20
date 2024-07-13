package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ReqresTests extends TestBaseApi {

    @Test
    void successfulGetSingleUserTest() {

        Response response = given()
                .log().uri()
                .log().method()

                .when()
                .get("/users/2")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-id-response-schema.json"))
                .extract().response();

        assertThat(response.path("data.id"), is(2));
        assertThat(response.path("data.email"), is("janet.weaver@reqres.in"));
        assertThat(response.path("data.first_name"), is("Janet"));
        assertThat(response.path("data.last_name"), is("Weaver"));
        assertThat(response.path("data.avatar"), is("https://reqres.in/img/faces/2-image.jpg"));
        assertThat(response.path("support.url"), is("https://reqres.in/#support-heading"));
        assertThat(response.path("support.text"), is("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }

    @Test
    void notFoundUserTest() {
        Response response = given()
                .log().uri()
                .log().method()

                .when()
                .get("/user/23")

                .then()
                .log().status()
                .log().body()
                .statusCode(404)
                .extract().response();

        assertThat(response.getBody().asString(), is(equalTo("{}")));
    }


    @Test
    void createUserTest() {

        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"name\": \"morpheus\", \"job\": \"leader\" }")

                .when()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/create-user-schema.json"))
                .extract().response();

        assertThat(response.path("name"), is("morpheus"));
        assertThat(response.path("job"), is("leader"));
    }

    @Test
    void updateUserTest() {

        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"name\": \"morpheus\", \"job\": \"zion resident\" }")

                .when()
                .put("/users/2")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/update-user-schema.json"))
                .extract().response();

        assertThat(response.path("name"), is("morpheus"));
        assertThat(response.path("job"), is("zion resident"));
    }

    @Test
    void deleteUserTest() {
        given()
                .log().uri()
                .log().method()

                .when()
                .delete("/users/2")

                .then()
                .log().status()
                .statusCode(204)
                .extract().response();
    }

    @Test
    void successfulRegisterUserTest() {

        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\" }")

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/successful-register-user-schema.json"))
                .extract().response();

        assertThat(response.path("id"), is(4));
        assertThat(response.path("token"), is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    void unsuccessfulRegisterUserTest() {

        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"email\": \"sydney@fife\" }")

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("schemas/unsuccessful-register-user-schema.json"))
                .extract().response();

        assertThat(response.path("error"), is("Missing password"));
    }

    @Test
    void successfulLoginTest() {

        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }")

                .when()
                .post("/login")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/successful-login-schema.json"))
                .extract().response();

        assertThat(response.path("token"), is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    void unsuccessfulLoginTest() {

        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"email\": \"peter@klaven\" }")

                .when()
                .post("/login")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("schemas/unsuccessful-login-schema.json"))
                .extract().response();

        assertThat(response.path("error"), is("Missing password") );
    }
}


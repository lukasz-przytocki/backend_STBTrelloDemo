package board;

import base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest extends BaseTest {

    @Test
    public void createNewBoard() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "My second board")
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();
        assertEquals("My second board", jsonPath.get("name"));

        String boardId = jsonPath.get("id");

        given()
                .spec(reqSpec)
                .when()
                .delete("https://api.trello.com/1/boards/" + boardId)
                .then()
                .statusCode(200);
    }

    @Test
    public void createNewBoardWithEmptyName() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "")
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(400)
                .extract()
                .response();
    }

    @Test
    public void createNewBoardWithoutDefaultLists() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "Board without default board list")
                .queryParam("defaultLists",false)
                .spec(reqSpec)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();



        JsonPath jsonPath = response.jsonPath();
        assertEquals("Board without default board list", jsonPath.get("name"));
        String boardId = jsonPath.get("id");

        Response responseGet = given()
                .spec(reqSpec)
                .when()
                .get("https://api.trello.com/1/boards/" + boardId+"/lists")
                .then()
                .statusCode(200)
                        .extract().response();

        JsonPath jsonPathGet = responseGet.jsonPath();
        List<String> idList = jsonPathGet.getList("id");
        assertEquals(0, idList.size());

        given()
                .spec(reqSpec)
                .when()
                .delete("https://api.trello.com/1/boards/" + boardId)
                .then()
                .statusCode(200);
    }

    @Test
    public void createNewBoardWithDefaultLists() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "Board with default board list")
                .queryParam("defaultLists",true)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();



        JsonPath jsonPath = response.jsonPath();
        assertEquals("Board with default board list", jsonPath.get("name"));
        String boardId = jsonPath.get("id");

        Response responseGet = given()
                .spec(reqSpec)
                .when()
                .get("https://api.trello.com/1/boards/" + boardId+"/lists")
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPathGet = responseGet.jsonPath();
        List<String> idList = jsonPathGet.getList("name");
        List<String> nameList = jsonPathGet.getList("name");
        jsonPathGet.prettyPrint();

        assertEquals(3,idList.size());
        assertEquals("Do zrobienia",nameList.get(0));
        assertEquals("W trakcie",nameList.get(1));
        assertEquals("Zrobione",nameList.get(2));

        given()
                .spec(reqSpec)
                .when()
                .delete("https://api.trello.com/1/boards/" + boardId)
                .then()
                .statusCode(200);
    }
}

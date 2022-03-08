package board;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class BoardTestAssertJ {
    private final String key = "94e8072c1d1178bd66cdcaebe0c14379";
    private final String token = "60cc29227ff4b31bec5a6fd13db5710c2f3309431a9f557e25003b8cb7638926";

    @Test
    public void createNewBoard() {
        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "My second board")
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();
        Assertions.assertThat(jsonPath.getString("name")).contains("My second board");

        String boardId = jsonPath.get("id");

        removingBoard(boardId);

//        given()
//                .queryParam("key", key)
//                .queryParam("token", token)
//                .contentType(ContentType.JSON)
//                .when()
//                .delete("https://api.trello.com/1/boards/" + boardId)
//                .then()
//                .statusCode(200);
    }

    @Test
    public void createNewBoardWithEmptyName() {
        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "")
                .contentType(ContentType.JSON)
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
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "Board without default board list")
                .queryParam("defaultLists", false)
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();


        JsonPath jsonPath = response.jsonPath();
        Assertions.assertThat(jsonPath.getString("name")).isEqualTo("Board without default board list");
        String boardId = jsonPath.get("id");

        Response responseGet = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .get("https://api.trello.com/1/boards/" + boardId + "/lists")
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPathGet = responseGet.jsonPath();
        List<String> idList = jsonPathGet.getList("id");
        Assertions.assertThat(idList.size()).isEqualTo(0);

        removingBoard(boardId);
        //        given()
//                .queryParam("key", key)
//                .queryParam("token", token)
//                .contentType(ContentType.JSON)
//                .when()
//                .delete("https://api.trello.com/1/boards/" + boardId)
//                .then()
//                .statusCode(200);
    }

    @Test
    public void createNewBoardWithDefaultLists() {
        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "Board with default board list")
                .queryParam("defaultLists", true)
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();
        Assertions.assertThat(jsonPath.getString("name")).isEqualTo("Board with default board list");
        String boardId = jsonPath.get("id");

        Response responseGet = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .get("https://api.trello.com/1/boards/" + boardId + "/lists")
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPathGet = responseGet.jsonPath();

        List<String> nameList = jsonPathGet.getList("name");
        jsonPathGet.prettyPrint();
        Assertions.assertThat(nameList).hasSize(3).contains("Do zrobienia", "W trakcie", "Zrobione");

        removingBoard(boardId);
//        given()
//                .queryParam("key", key)
//                .queryParam("token", token)
//                .contentType(ContentType.JSON)
//                .when()
//                .delete("https://api.trello.com/1/boards/" + boardId)
//                .then()
//                .statusCode(200);
    }

    private void removingBoard(String boardId){
        given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("https://api.trello.com/1/boards/" + boardId)
                .then()
                .statusCode(200);
    }
}

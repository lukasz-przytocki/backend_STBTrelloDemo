package organizations;

import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class OrganizationsTest extends BaseTest {

    @Test
    public void createNewOrganization(){
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName","Test_oraganization")
                .queryParam("desc", "Organization for test purpose")
                .queryParam("name","abc")
                .queryParam("website", "https://github.com/")
                .when()
                .post(BASIC_URL+"/"+ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();
        jsonPath.prettyPrint();
        Assertions.assertThat(jsonPath.getString("displayName").contains("Test_oraganization"));
        Assertions.assertThat(jsonPath.getString("desc").contains("Organization for test purpose"));
        Assertions.assertThat(jsonPath.getString("name").contains("abc"));
        Assertions.assertThat(jsonPath.getString("website").contains("https://github.com/"));
    }

    @Test
    public void checkIfDisplayNameIsRequired(){
        Response response = given()
                .spec(reqSpec)
//                .queryParam("displayName","Test_oraganization")
                .queryParam("desc", "Organization for test purpose")
                .queryParam("name","abc")
                .queryParam("website", "https://github.com/")
                .when()
                .post(BASIC_URL+"/"+ORGANIZATIONS)
                .then()
                .statusCode(400)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();
//        jsonPath.prettyPrint();
        Assertions.assertThat(jsonPath.getString("message").contains("Display Name must be at least 1 character"));
        Assertions.assertThat(jsonPath.getString("error").contains("ERROR"));
    }

    @Test
    public void checkIfTheNameIsToShort(){
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName","Test_oraganization2")
                .queryParam("desc", "Organization for test purpose")
                .queryParam("name","ab--c")
                .queryParam("website", "interia")
                .when()
                .post(BASIC_URL+"/"+ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();
        jsonPath.prettyPrint();

    }
}

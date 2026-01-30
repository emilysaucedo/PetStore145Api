package petstore;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.is;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;


public class TestUser {
    static String uri = "https://petstore.swagger.io/v2/user";
    static String ct = "application/json";
    static String token;

    @Test
    public static String login(){
        String username = "emily";
        String password = "123456";

        String resultadoEsperado = "logged in user session:";

        Response resposta = (Response) given()
            .contentType(ct)
            .log().all()
        .when()
            .get(uri + "/login?username=" + username + "&password=" + password)
        .then()
            .log().all()
            .statusCode(200)
            .body("message", containsString(resultadoEsperado))
        .extract();

        token = resposta.jsonPath().getString("message").substring(23);
        System.out.println("Conteudo do Token/API Key: " + token);
        return token;
    }

}

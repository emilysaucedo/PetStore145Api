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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import com.google.gson.Gson;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestUser {
    static String uri = "https://petstore.swagger.io/v2/user";
    static String ct = "application/json";
    static String token;
    static String userId = "149";
    static String username = "saucedo";
    static String firstName = "Emily";
    static String lastName = "Pires";
    static String newLastName = "Saucedo";
    static String email = "mily@mail.com";
    static String password = "123456";
    static String phone = "44999999999";
    static String newPhone = "4411111111";
    static int userStatus = 7534;


    //Funções de Apoio
    public String lerJson(String caminhoJson) throws IOException{
        return new String(Files.readAllBytes(Paths.get(caminhoJson)));
    }

    @Test @Order(1)
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

    @Test @Order(2)
    public void PostUser() throws IOException{
        String jsonBody = lerJson("data/user.json");

        given()
            .contentType(ct)
            .log().all()
            .body(jsonBody)
        .when()
            .post(uri)
        .then()
            .log().all()
            .statusCode(200)
            .body("message", is(userId));
    }

    @Test @Order(3)
    public void GetUser(){
        given()
            .contentType(ct)
            .log().all()
            .header("", "api_key: " + TestUser.login()) //Passar o token autenticação no cabeçalho 
            .when()
        .get(uri + "/" + username)
            .then()
            .log().all()
            .statusCode(200)
            .body("id", is(Integer.parseInt(userId)))
            .body("username", is(username))
            .body("firstName", is(firstName))
            .body("lastName", is(lastName))
            .body("email", is(email))
            .body("password", is(password))
            .body("phone", is(phone))
            .body("userStatus", is(userStatus));
    }

    @Test @Order(4)
    public void PutUser() throws IOException{
        String jsonBody = lerJson("data/putuser.json");

        given()
            .contentType(ct)
            .log().all()
            .body(jsonBody)
        .when()
            .put(uri + "/" + username)
        .then()
            .log().all()
            .statusCode(200);
    }

    @Test @Order(5)
    public void DeleteUser(){
        given()
            .contentType(ct)
            .log().all()
        .when()
            .delete(uri + "/" + username)
        .then()
            .log().all()
            .statusCode(200);
    }

    //DDT - Data Driven Testing
    @ParameterizedTest @Order(6)
    @CsvFileSource(resources = "/user.csv", numLinesToSkip = 1, delimiter = ',')
    public void PostUserDDT(
        int id,
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        String phone,
        int userStatus
    )
    {
        User user = new User();
        user.id = id;
        user.username = username;
        user.firstName = firstName;
        user.lastName = lastName;
        user.email = email;
        user.password = password;
        user.phone = phone;
        user.userStatus = userStatus;

        Gson gson = new Gson();
        String jsonBody = gson.toJson(user);

        given()
            .contentType(ct)
            .log().all()
            .body(jsonBody)
        .when()
            .post(uri)
        .then()
            .log().all()
            .statusCode(200)
            .body("message", is(String.valueOf(id)));
    }

}

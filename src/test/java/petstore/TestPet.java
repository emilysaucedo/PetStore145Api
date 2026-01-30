//Pacote
package petstore;

//Imports
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;

import java.beans.Transient;
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

//Classe
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestPet {
    //Atributos
    String uri = "https://petstore.swagger.io/v2/pet";
    String baseUrl = "https://petstore.swagger.io/v2";
    String contentType = "application/json";
    static int petId = 178999;
    static String petName = "ratazana";
    static String categoryName = "dog";
    static String tag0Name = "vacinado";
    static String updateStatus = "sold";


    //Funções de Apoio
    public String lerJson(String caminhoJson) throws IOException{
        return new String(Files.readAllBytes(Paths.get(caminhoJson)));
    }

    //Métodos e Funções
    //Incluir - Create - Post
    @Test @Order(1)
    public void PostPet() throws IOException{
        String jsonBody = lerJson( "data/pet.json");

        given()
            .contentType(contentType) //Formato do dado
            .log().all() //Exibir o envio
            .body(jsonBody) //Dado a ser enviado
            .when()
        .post(uri)
            .then()
            .log().all() //Exibir a resposta
            .statusCode(200) //Status code esperado, comunicação
            .body("name", is(petName));
    }

    @Test @Order(2)
    public void GetPet(){
        given()
            .contentType(contentType)
            .log().all()
            .header("", "api_key: " + TestUser.login()) //Passar o token autenticação no cabeçalho 
            .when()
        .get(uri + "/" + petId)
            .then()
            .log().all()
            .statusCode(200)
            .body("name", is(petName))
            .body("id", is(petId))
            .body("category.name", is (categoryName))
            .body("tags[0].name", is(tag0Name));
    }

    @Test @Order(3)
    public void PutPet() throws IOException{
        String jsonBody = lerJson( "data/petput.json");

        given()
            .contentType(contentType) //Formato do dado
            .log().all() //Exibir o envio
            .body(jsonBody) //Dado a ser enviado
        .when()
            .put(uri)
        .then()
            .log().all() //Exibir a resposta
            .statusCode(200) //Status code esperado, comunicação
            .body("name", is(petName))
            .body("id", is(petId))
            .body("category.name", is (categoryName))
            .body("tags[0].name", is(tag0Name))
            .body("status", is(updateStatus));
    }

    @Test @Order(4)
    public void DeletePet(){
        given()
            .contentType(contentType)
            .log().all()
        .when()
            .delete(uri + "/" + petId)
        .then()
            .log().all()
            .statusCode(200)
            .body("code", is(200))
            .body("type", is("unknown"))
            .body("message", is(String.valueOf(petId)));
    }

    // DDT - Data Driven Testing
    // Teste com JSON (body) parametrizado
    @ParameterizedTest @Order(5)
    @CsvFileSource(resources = "/pets.csv", numLinesToSkip = 1, delimiter = ',')
    public void PostPetDDT(
        int petId,
        String petName,
        int catId,
        String catName,
        String status1,
        String status2
    )
    {
        //Criar a classe pet para receber os dados


        Pet pet = new Pet();
        Pet.Category category = pet.new Category(); //Instanciar a subclasse Category
        Pet.Tag[] tags = new Pet.Tag[2]; //Instanciar o array de tags com 2 espaços
        pet.id = petId;
        pet.category = category;
        pet.category.id = catId;
        pet.category.name = catName;
        pet.name = petName;
        pet.tags = tags;
        tags[0] = pet.new Tag();
        tags[1] = pet.new Tag();
        pet.tags[0].id = 1;
        pet.tags[0].name = "vacinado";
        pet.tags[1].id = 2;
        pet.tags[1].name = "vermifugado";
        pet.status = status1;

        //Criar json a partir do objeto pet preenchido com os dados
        Gson gson = new Gson();
        String jsonBody = gson.toJson(pet);

        given()
            .contentType(contentType) //Formato do dado
            .log().all() //Exibir o envio
            .body(jsonBody) //Dado a ser enviado
        .when()
            .post(uri)
        .then()
            .log().all() //Exibir a resposta
            .statusCode(200) //Status code esperado, comunicação
            .body("id", is(petId))
            .body("name", is(petName))
            .body("category.id", is(catId))
            .body("category.name", is(catName))
            .body("status", is(status1));  
    }

}
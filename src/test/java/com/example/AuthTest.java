package com.example;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class AuthTest {

    private static final String BASE_URI = "https://restful-booker.herokuapp.com";

    @Test
    public void testCreateAuthTokenHappyPath() {
        String endpoint = "/auth";

        // Envío de solicitud POST para obtener un token
        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body("{ \"username\": \"admin\", \"password\": \"password123\" }")
                .post(endpoint);

        int statusCode = response.getStatusCode();
        String token = response.jsonPath().getString("token");

        // Aserciones
        Assert.assertEquals(statusCode, 200);
        Assert.assertNotNull(token, "El token no debe ser nulo o vacío");
    }

    @Test
    public void testCreateAuthTokenUnhappyPath() {
        String endpoint = "/auth";

        // Envío de solicitud POST para obtener un token
        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body("{ \"username\": \"admin\", \"password\": \"prueba123\" }")
                .post(endpoint);

        int statusCode = response.getStatusCode();
        String token = response.jsonPath().getString("token");

        // Aserciones
        if (statusCode == 200) {
            Assert.assertNotNull(token, "El token no debe ser nulo o vacío");
        } else {
            // Si el código de estado no es 200, asumimos que hay un error
            Assert.assertNull(token, "El token resulta nulo");
            String reason = response.jsonPath().getString("reason");
            Assert.fail("Error en la autenticación. Razón: " + reason);
        }
    }

}

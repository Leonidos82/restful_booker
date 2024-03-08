package com.example;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class PingTest {

    private static final String BASE_URI = "https://restful-booker.herokuapp.com";

    @Test
    public void testHealthCheckHappyPath() {
        Response response = RestAssured.get(BASE_URI + "/ping");

        Assert.assertEquals(response.getStatusCode(), 201, "El código de estado debería ser 201");
        Assert.assertEquals(response.getBody().asString(), "Created", "El mensaje de respuesta debería ser 'Created'");
    }

    @Test
    public void testHealthCheckUnhappyPath() {
        // Intentando hacer un ping a una URL inexistente
        Response response = RestAssured.get("https://restful-booker.herokuapp.com/nonexistent");

        Assert.assertNotEquals(response.getStatusCode(), 201, "El código de estado debería ser diferente de 201 para una URL inexistente");
    }

    @Test
    public void testHealthCheckUnhappyPathInvalidMethod() {
        // Intentando hacer una solicitud utilizando un método no permitido (POST en lugar de GET)
        Response response = RestAssured.post(BASE_URI + "/ping");

        Assert.assertNotEquals(response.getStatusCode(), 201, "El código de estado debería ser diferente de 201 para un método no permitido");
    }

}

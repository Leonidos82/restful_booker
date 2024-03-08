package com.example;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class BookingTest {

    private static final String BASE_URI = "https://restful-booker.herokuapp.com";

    @Test
    public void testGetBookingIdsHappyPath() {
        // Caso 1: Sin parámetros opcionales
        Response responseWithoutParams = RestAssured.get(BASE_URI + "/booking");
        List<Integer> bookingIdsWithoutParams = responseWithoutParams.jsonPath().getList("bookingid");

        int statusCodeWithoutParams = responseWithoutParams.getStatusCode();

        // Aserciones para el caso sin parámetros opcionales
        Assert.assertEquals(statusCodeWithoutParams, 200);
        Assert.assertNotNull(bookingIdsWithoutParams, "Lista de bookingIds no debe ser nula");
        Assert.assertFalse(bookingIdsWithoutParams.isEmpty(), "Lista de bookingIds no debe estar vacía");

        // Caso 2: Con firstname y lastname
        Response responseWithNames = RestAssured.given()
                .param("firstname", "John")
                .param("lastname", "Doe")
                .get(BASE_URI + "/booking");
        List<Integer> bookingIdsWithNames = responseWithNames.jsonPath().getList("bookingid");

        int statusCodeWithNames = responseWithNames.getStatusCode();

        // Aserciones para el caso con firstname y lastname
        Assert.assertEquals(statusCodeWithNames, 200);
        Assert.assertNotNull(bookingIdsWithNames, "Lista de bookingIds no debe ser nula");
        Assert.assertFalse(bookingIdsWithNames.isEmpty(), "Lista de bookingIds no debe estar vacía");

        // Caso 3: Con checkin y checkout
        Response responseWithDates = RestAssured.given()
                .param("checkin", "2022-03-01")
                .param("checkout", "2022-03-15")
                .get(BASE_URI + "/booking");
        List<Integer> bookingIdsWithDates = responseWithDates.jsonPath().getList("bookingid");

        int statusCodeWithDates = responseWithDates.getStatusCode();

        // Aserciones para el caso con checkin y checkout
        Assert.assertEquals(statusCodeWithDates, 200);
        Assert.assertNotNull(bookingIdsWithDates, "Lista de bookingIds no debe ser nula");
        Assert.assertFalse(bookingIdsWithDates.isEmpty(), "Lista de bookingIds no debe estar vacía");
    }

    @Test
    public void testGetBookingIdsUnHappyPath() {
        // Caso 1: Sin parámetros opcionales
        Response responseWithoutParams = RestAssured.get(BASE_URI + "/booking");
        List<Integer> bookingIdsWithoutParams = responseWithoutParams.jsonPath().getList("bookingid");

        int statusCodeWithoutParams = responseWithoutParams.getStatusCode();

        Assert.assertNotEquals(statusCodeWithoutParams, 200, "El código de estado debería ser diferente de 200");
        Assert.assertNull(bookingIdsWithoutParams, "La lista es nula");

        // Caso 2: Con firstname y lastname
        Response responseWithNames = RestAssured.given()
                .param("firstname", "John")
                .param("lastname", "Doe")
                .get(BASE_URI + "/booking");
        List<Integer> bookingIdsWithNames = responseWithNames.jsonPath().getList("bookingid");

        int statusCodeWithNames = responseWithNames.getStatusCode();

        Assert.assertNotEquals(statusCodeWithNames, 200, "El código de estado debería ser diferente de 200");
        Assert.assertNull(bookingIdsWithNames, "La lista es nula");

        // Caso 3: Con checkin y checkout
        Response responseWithDates = RestAssured.given()
                .param("checkin", "2022-03-01")
                .param("checkout", "2022-03-15")
                .get(BASE_URI + "/booking");
        List<Integer> bookingIdsWithDates = responseWithDates.jsonPath().getList("bookingid");

        int statusCodeWithDates = responseWithDates.getStatusCode();

        Assert.assertNotEquals(statusCodeWithDates, 200, "El código de estado debería ser diferente de 200");
        Assert.assertNull(bookingIdsWithDates, "La lista es nula");

    }

    @Test
    public void testGetBookingHappyPath() {
        // Supongamos que hay un booking creado previamente con el ID 2
        int bookingId = 2;

        Response response = RestAssured.get(BASE_URI + "/booking/" + bookingId);
        String firstname = response.jsonPath().getString("firstname");
        String lastname = response.jsonPath().getString("lastname");
        int totalprice = response.jsonPath().getInt("totalprice");
        Boolean depositpaid = response.jsonPath().getBoolean("depositpaid");
        String bookingdates_checkin = response.jsonPath().getString("bookingdates.checkin");
        String bookingdates_checkout = response.jsonPath().getString("bookingdates.checkout");
        String additionalneeds = response.jsonPath().getString("additionalneeds");
        ;

        // Aserciones
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNotNull(firstname, "El firstname no debe ser nulo");
        Assert.assertNotNull(lastname, "El lastname no debe ser nulo");
        Assert.assertTrue(totalprice > 0, "El totalprice debe ser mayor que 0");
        Assert.assertNotNull(depositpaid, "El depositpaid no debe ser nulo");
        Assert.assertNotNull(bookingdates_checkin, "El bookingdates_checkin no debe ser nulo");
        Assert.assertNotNull(bookingdates_checkout, "El bookingdates_checkout no debe ser nulo");
        if (additionalneeds != null) {
            Assert.assertFalse(additionalneeds.isEmpty(), "El additionalneeds no debe ser vacio");
        }

    }

    @Test
    public void testGetBookingUnHappyPath() {
        // Supongamos que no hay un booking creado previamente con el ID 10000
        int bookingId = 10000;

        Response response = RestAssured.get(BASE_URI + "/booking/" + bookingId);

        Assert.assertNotEquals(response.getStatusCode(), 200, "El código de estado debería ser diferente de 200");
        Assert.assertEquals(response.asString(), "Not Found");
    }

    @Test
    public void testCreateBookingHappyPath() {
        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body("{ \"firstname\": \"Jim\", \"lastname\": \"Brown\", \"totalprice\": 111, " +
                        "\"depositpaid\": true, \"bookingdates\": { \"checkin\": \"2018-01-01\", " +
                        "\"checkout\": \"2019-01-01\" }, \"additionalneeds\": \"Breakfast\" }")
                .post("/booking");

        Assert.assertEquals(response.getStatusCode(), 200);

        int bookingId = response.jsonPath().getInt("bookingid");
        Assert.assertTrue(bookingId > 0, "El bookingId debe ser mayor que 0");
        Assert.assertEquals(response.contentType(), "application/json", "La respuesta debería ser de tipo JSON");
    }

    @Test
    public void testCreateBookingUnHappyPath() {
        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body("{ \"firstname\": \"Jim\", \"lastname\": \"Brown\", " +
                        "\"depositpaid\": true, \"bookingdates\": { \"checkin\": \"2018-01-01\", " +
                        "\"checkout\": \"2019-01-01\" }, \"additionalneeds\": \"Breakfast\" }")
                .post("/booking");

        Assert.assertNotEquals(response.getStatusCode(), 200, "El código de estado debería ser diferente de 200");

        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("Error"), "La respuesta debería contener información de error");
        String errorMessage = response.asString();
        Assert.assertNotNull(errorMessage, "Debería haber un mensaje de error en la respuesta");
    }

    @Test
    public void testUpdateBookingHappyPath() {
        int existingBookingId = 2;
        // Datos de actualización
        String updatedFirstname = "James";
        String updatedLastname = "Brown";
        int updatedTotalPrice = 111;
        boolean updatedDepositPaid = true;
        String updatedCheckinDate = "2018-01-01";
        String updatedCheckoutDate = "2019-01-01";
        String updatedAdditionalNeeds = "Breakfast";

        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .pathParam("id", existingBookingId)
                .body("{ " +
                        "\"firstname\": \"" + updatedFirstname + "\", " +
                        "\"lastname\": \"" + updatedLastname + "\", " +
                        "\"totalprice\": " + updatedTotalPrice + ", " +
                        "\"depositpaid\": " + updatedDepositPaid + ", " +
                        "\"bookingdates\": { " +
                            "\"checkin\": \"" + updatedCheckinDate + "\", " +
                            "\"checkout\": \"" + updatedCheckoutDate + "\" " +
                        "}, " +
                        "\"additionalneeds\": \"" + updatedAdditionalNeeds + "\" " +
                        "}")
                .put("/booking/{id}");



        Assert.assertEquals(response.getStatusCode(), 200, "El código de estado debería ser 200");

        // Aserciones del cuerpo de la respuesta
        Assert.assertEquals(response.jsonPath().getString("firstname"), updatedFirstname);
        Assert.assertEquals(response.jsonPath().getString("lastname"), updatedLastname);
        Assert.assertEquals(response.jsonPath().getInt("totalprice"), updatedTotalPrice);
        Assert.assertEquals(response.jsonPath().getBoolean("depositpaid"), updatedDepositPaid);
        Assert.assertEquals(response.jsonPath().getString("bookingdates.checkin"), updatedCheckinDate);
        Assert.assertEquals(response.jsonPath().getString("bookingdates.checkout"), updatedCheckoutDate);
        Assert.assertEquals(response.jsonPath().getString("additionalneeds"), updatedAdditionalNeeds);
    }

    @Test
    public void testUpdateBookingUnhappyPath() {
        int existingBookingId = 1786;
        // Se intenta actualizar un booking con datos incorrectos (sin el campo "firstname")
        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .pathParam("id", existingBookingId)
                .body("{ " +
                        "\"lastname\": \"Brown\", " +
                        "\"totalprice\": 111, " +
                        "\"depositpaid\": true, " +
                        "\"bookingdates\": { " +
                            "\"checkin\": \"2018-01-01\", " +
                            "\"checkout\": \"2019-01-01\" " +
                        "}, " +
                        "\"additionalneeds\": \"Breakfast\" " +
                        "}")
                .put("/booking/{id}");

        Assert.assertNotEquals(response.getStatusCode(), 200, "El código de estado debería ser diferente de 200");
    }

    @Test
    public void testPartialUpdateBookingHappyPath() {
        int existingBookingId = 2;
        // Datos de actualización parcial
        String updatedFirstname = "James";
        String updatedLastname = "Brown";

        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .pathParam("id", existingBookingId)
                .body("{ " +
                        "\"firstname\": \"" + updatedFirstname + "\", " +
                        "\"lastname\": \"" + updatedLastname + "\" " +
                        "}")
                .patch("/booking/{id}");

        Assert.assertEquals(response.getStatusCode(), 200, "El código de estado debería ser 200");

        // Aserciones del cuerpo de la respuesta
        Assert.assertEquals(response.jsonPath().getString("firstname"), updatedFirstname);
        Assert.assertEquals(response.jsonPath().getString("lastname"), updatedLastname);
    }

    @Test
    public void testPartialUpdateBookingUnhappyPath() {
        int existingBookingId = 2;
        // Se intenta realizar una actualización parcial sin proporcionar datos obligatorios y un token incorrecto
        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .accept("application/json")
                .header("Cookie", "token=49d2786ba79b346") 
                .pathParam("id", existingBookingId)
                .body("{}") // Intenta actualizar sin proporcionar datos obligatorios
                .patch("/booking/{id}");

        Assert.assertNotEquals(response.getStatusCode(), 200, "El código de estado debería ser diferente de 200");

        // Puedes agregar más aserciones según los casos específicos que desees manejar
    }

    @Test
    public void testDeleteBookingHappyPath() {
        // Supongamos que hay un booking creado previamente con el ID 1
        int bookingId = 1;

        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .delete("/booking/" + bookingId);

        Assert.assertEquals(response.getStatusCode(), 201, "El código de estado debería ser 201");
        Assert.assertEquals(response.getBody().asString(), "Created", "El mensaje de respuesta debería ser 'Created'");
    }

    @Test
    public void testDeleteBookingUnhappyPathNoAuthorization() {
        // Supongamos que hay un booking creado previamente con el ID 1
        int bookingId = 1;

        Response response = RestAssured.delete(BASE_URI + "/booking/" + bookingId);

        Assert.assertNotEquals(response.getStatusCode(), 201, "El código de estado debería ser diferente de 201 sin autorización");
    }

    @Test
    public void testDeleteBookingUnhappyPathInvalidId() {

        // Intenta eliminar un booking con un ID no existente
        int nonExistingBookingId = 10000;

        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .delete("/booking/" + nonExistingBookingId);

        Assert.assertNotEquals(response.getStatusCode(), 201, "El código de estado debería ser diferente de 201 para un ID no existente");
    }

}

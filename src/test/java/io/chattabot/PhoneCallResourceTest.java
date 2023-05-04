package io.chattabot;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PhoneCallResourceTest {

    @Test
    public void testMainEndpoint() {
        given()
          .when().get("/")
          .then()
             .statusCode(200)
             .body(is("Phone Call"));
    }
    
    @Test
    public void testWebhookEndpoint() {
        given()
          .when().post("/")
          .then()
             .statusCode(200)
             .body(is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Say voice=\"alice\">Hi! Thanks for calling, but we prefer text. We are texting you now.</Say></Response>"));
    }
    
    @Test
    public void testWebhookEndpointWithParams() {
        given().formParam("From", "+15551231234")
               .formParam("To", "+15551232222")
               .when().post("/")
               .then()
               .statusCode(200)
               .body(is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Say voice=\"alice\">Hi! Thanks for calling, but we prefer text. We are texting you now.</Say></Response>"));
    }

}
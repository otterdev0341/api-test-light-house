package api_test.auth

import helper.AUTH
import helper.UrlManagement
import helper.user.GenerateUserUtility
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Step
import io.restassured.response.Response
import spock.lang.Specification
import io.qameta.allure.Allure
import static io.restassured.RestAssured.*
import io.restassured.http.ContentType
import static io.restassured.matcher.RestAssuredMatchers.*
import static org.hamcrest.Matchers.*


@Epic("REST API Tests") // Overall epic for these tests
@Feature("Register User")
class Register extends Specification {

    @Step("Create new User must be success")
    def "Create new User must be success"() {
        given:
            def create_user_url = UrlManagement.getAuthUrl(AUTH.SignUp)
            def new_user = GenerateUserUtility.generateAlwaysNewValidUser()
            // Optional: Attach the request payload to the Allure report
            Allure.addAttachment("Request Payload - Create User", "application/json", new_user.toString(), ".json")
        when: "a POST request is sent to create the new user"
        Response response = given()
                .contentType(ContentType.JSON) // Set the content type of the request body
                .body(new_user)                // REST Assured will serialize this to JSON
                .log().all()                   // Log the request details
                .when()
                .post(create_user_url)

        then: "the response status code is 201 (Created) and the response body is as expected"
        response.then()
                .log().all()                   // Log the response details
                .statusCode(201)

        // Optional: Attach the response to the Allure report
        Allure.addAttachment("Response - Create User", "application/json", response.asString(), ".json")

    }
}
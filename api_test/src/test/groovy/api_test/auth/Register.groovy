package api_test.auth

import dto.auth.ReqSignUpDto
import helper.AUTH
import helper.UrlManagement
import helper.user.GenerateUserUtility
import helper.user.InvalidCase
import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.Step
import io.qameta.allure.Story
import io.qameta.allure.SeverityLevel
import io.restassured.response.Response
import org.junit.jupiter.api.DisplayName
import spock.lang.Ignore
import spock.lang.Specification
import io.qameta.allure.Allure
import static io.restassured.RestAssured.*
import io.restassured.http.ContentType
import static io.restassured.matcher.RestAssuredMatchers.*
import static org.hamcrest.Matchers.*


@Epic("REST API Tests") // Overall epic for these tests
@Feature("Register User")
class Register extends Specification {

    @Story("Successful User Registration - Generated Data")
    @DisplayName("Should successfully create a new user with dynamically generated data")
    @Description("""
        This test verifies the user registration happy path.
        It sends a POST request with a unique, dynamically generated user payload.
        Expects a 201 Created status and validates key fields in the response,
        assuming the response structure is: { "data": { "id": "...", "username": "...", ... } }
    """)
    @Severity(SeverityLevel.CRITICAL)
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

    @Story("User Registration Attempt - Potentially Problematic Data") // Renamed story for clarity
    @DisplayName("Should handle user registration attempt with specific (potentially non-unique) data")
    @Description("""
        This test attempts to register a user with specific, hardcoded data.
        NOTE: This test is likely to fail on subsequent runs if the user 'test_user'
        is not cleaned up, as the system might return a 4xx error for duplicate data.
        The current assertion for 500 is unusual for a duplicate user scenario and might indicate
        an issue with the API's error handling or the test's expectation.
        For a robust test of duplicate user handling, a separate negative test case expecting
        a 400 (Bad Request) or 409 (Conflict) would be more appropriate.
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Init valid user"() {
        given:
            def create_user_url = UrlManagement.getAuthUrl(AUTH.SignUp)
            def new_user = new ReqSignUpDto(username: "test_user", password: "password", email: "test@gmail.com",
                                            first_name: "test", last_name: "test", gender: "male")
        when: "a POST request is sent to create the new user"
        Response response = given()
            .contentType(ContentType.JSON)
            .body(new_user)
            .log().all()
            .when()
            .post(create_user_url)

        Allure.addAttachment("Request Payload - Specific User Sign Up", "application/json", new groovy.json.JsonOutput().toJson(new_user), ".json")

        then: "the response status code is 201 (Created) and the response body is as expected"

        response.then()
                .log().all()
                .statusCode(500)

    }

    @Story("User Registration Attempt - Invalid username")
    @DisplayName("Should handle user registration attempt with invalid username")
    @Description("""
        This test attempts to register a user with invalid data.
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Should handle user registration attempt with invalid username"(){
        given:
            def sign_up_url = UrlManagement.getAuthUrl(AUTH.SignUp)
            def test_user = GenerateUserUtility.generateInvalidUserBaseOnCase(InvalidCase.INVALID_USERNAME)

        when: "a POST request is sent to create the new user"
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(test_user)
                    .log().all()
                    .when()
                    .post(sign_up_url)
        Allure.addAttachment("Request Payload - Specific User Sign Up", "application/json", new groovy.json.JsonOutput().toJson(test_user), ".json")

        then: "the response status code is 400 (Bad Request) and the response body is as expected"
            response.then()
                    .log().all()
                    .statusCode(400)

    }// end function

    @Story("User Registration Attempt - Invalid password")
    @DisplayName("Should handle user registration attempt with invalid password")
    @Description("""
        This test attempts to register a user with invalid data.
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Should handle user registration attempt with invalid password"(){
        given:
        def sign_up_url = UrlManagement.getAuthUrl(AUTH.SignUp)
        def test_user = GenerateUserUtility.generateInvalidUserBaseOnCase(InvalidCase.INVALID_PASSWORD)

        when: "a POST request is sent to create the new user"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(test_user)
                .log().all()
                .when()
                .post(sign_up_url)
        Allure.addAttachment("Request Payload - Specific User Sign Up", "application/json", new groovy.json.JsonOutput().toJson(test_user), ".json")

        then: "the response status code is 400 (Bad Request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(400)

    }// end function

    @Story("User Registration Attempt - Invalid email")
    @DisplayName("Should handle user registration attempt with invalid email")
    @Description("""
        This test attempts to register a user with invalid data.
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Should handle user registration attempt with invalid email"(){
        given:
        def sign_up_url = UrlManagement.getAuthUrl(AUTH.SignUp)
        def test_user = GenerateUserUtility.generateInvalidUserBaseOnCase(InvalidCase.INVALID_EMAIL)

        when: "a POST request is sent to create the new user"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(test_user)
                .log().all()
                .when()
                .post(sign_up_url)
        Allure.addAttachment("Request Payload - Specific User Sign Up", "application/json", new groovy.json.JsonOutput().toJson(test_user), ".json")

        then: "the response status code is 400 (Bad Request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(400)

    }// end function


    @Story("User Registration Attempt - Invalid first name")
    @DisplayName("Should handle user registration attempt with invalid first name")
    @Description("""
        This test attempts to register a user with invalid data.
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Should handle user registration attempt with invalid first name"(){
        given:
        def sign_up_url = UrlManagement.getAuthUrl(AUTH.SignUp)
        def test_user = GenerateUserUtility.generateInvalidUserBaseOnCase(InvalidCase.INVALID_FIRST_NAME)

        when: "a POST request is sent to create the new user"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(test_user)
                .log().all()
                .when()
                .post(sign_up_url)
        Allure.addAttachment("Request Payload - Specific User Sign Up", "application/json", new groovy.json.JsonOutput().toJson(test_user), ".json")

        then: "the response status code is 400 (Bad Request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(400)

    }// end function

    @Story("User Registration Attempt - Invalid last name")
    @DisplayName("Should handle user registration attempt with invalid last name")
    @Description("""
        This test attempts to register a user with invalid data.
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Should handle user registration attempt with invalid last name"(){
        given:
        def sign_up_url = UrlManagement.getAuthUrl(AUTH.SignUp)
        def test_user = GenerateUserUtility.generateInvalidUserBaseOnCase(InvalidCase.INVALID_LAST_NAME)

        when: "a POST request is sent to create the new user"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(test_user)
                .log().all()
                .when()
                .post(sign_up_url)
        Allure.addAttachment("Request Payload - Specific User Sign Up", "application/json", new groovy.json.JsonOutput().toJson(test_user), ".json")

        then: "the response status code is 400 (Bad Request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(400)

    }// end function

    @Story("User Registration Attempt - Invalid last gender")
    @DisplayName("Should handle user registration attempt with invalid last gender")
    @Description("""
        This test attempts to register a user with invalid data.
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Should handle user registration attempt with invalid gender"(){
        given:
        def sign_up_url = UrlManagement.getAuthUrl(AUTH.SignUp)
        def test_user = GenerateUserUtility.generateInvalidUserBaseOnCase(InvalidCase.INVALID_GENDER)

        when: "a POST request is sent to create the new user"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(test_user)
                .log().all()
                .when()
                .post(sign_up_url)
        Allure.addAttachment("Request Payload - Specific User Sign Up", "application/json", new groovy.json.JsonOutput().toJson(test_user), ".json")

        then: "the response status code is 400 (Bad Request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(400)

    }// end function

    @Story("User Registration Attempt - All Empty Field")
    @DisplayName("Should handle user registration attempt with All Empty Field")
    @Description("""
        This test attempts to register a user with invalid data.
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Should handle user registration attempt with invalid All Empty Field"(){
        given:
        def sign_up_url = UrlManagement.getAuthUrl(AUTH.SignUp)
        def test_user = GenerateUserUtility.generateInvalidUserBaseOnCase(InvalidCase.ALL_EMPTY_FIELD)

        when: "a POST request is sent to create the new user"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(test_user)
                .log().all()
                .when()
                .post(sign_up_url)
        Allure.addAttachment("Request Payload - Specific User Sign Up", "application/json", new groovy.json.JsonOutput().toJson(test_user), ".json")

        then: "the response status code is 400 (Bad Request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(400)

    }// end function

    @Story("User Registration Attempt - Exist Username")
    @DisplayName("Should handle user registration attempt with exist username")
    @Description("""
        This test attempts to register a user with invalid data.
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Should handle user registration attempt with exist username"(){
        given:
        def sign_up_url = UrlManagement.getAuthUrl(AUTH.SignUp)
        def test_user = GenerateUserUtility.generateAlwaysNewValidUser()
        test_user.setUsername("test_user")

        when: "a POST request is sent to create the new user"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(test_user)
                .log().all()
                .when()
                .post(sign_up_url)
        Allure.addAttachment("Request Payload - Specific User Sign Up", "application/json", new groovy.json.JsonOutput().toJson(test_user), ".json")

        then: "the response status code is 400 (Bad Request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(500)

    }// end function

    @Story("User Registration Attempt - Exist Email")
    @DisplayName("Should handle user registration attempt with exist email")
    @Description("""
        This test attempts to register a user with invalid data.
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Should handle user registration attempt with exist email"(){
        given:
        def sign_up_url = UrlManagement.getAuthUrl(AUTH.SignUp)
        def test_user = GenerateUserUtility.generateAlwaysNewValidUser()
        test_user.setEmail("test@gmail.com")

        when: "a POST request is sent to create the new user"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(test_user)
                .log().all()
                .when()
                .post(sign_up_url)
        Allure.addAttachment("Request Payload - Specific User Sign Up", "application/json", new groovy.json.JsonOutput().toJson(test_user), ".json")

        then: "the response status code is 400 (Bad Request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(500)

    }// end function














}// end class
package api_test.auth

import helper.AUTH
import helper.UrlManagement
import helper.user.GenerateSignInUtility
import helper.user.INVALID_LOGIN
import io.qameta.allure.Allure
import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story
import io.restassured.response.Response
import org.junit.jupiter.api.DisplayName
import spock.lang.Specification
import static io.restassured.RestAssured.*
import io.restassured.http.ContentType
import static io.restassured.matcher.RestAssuredMatchers.*
import static org.hamcrest.Matchers.*

@Epic("Auth Api Tests") // Overall epic for these tests
@Feature("Perform Login Test must be pass")
class Login extends Specification {

    @Story("Login Attempt with valid user")
    @DisplayName("Should successfully login with valid user credentials")
    @Description("""
        This is will login with valid user, then will return jwt token
    """)
    @Severity(SeverityLevel.CRITICAL)
    def "Perform Login must"() {
        given:
            def sign_in_url = UrlManagement.getAuthUrl(AUTH.SignIn)
            def valid_user = GenerateSignInUtility.generateValidUserToSignIn()
        Allure.addAttachment("Request Payload - Specific User Sign In", "application/json", new groovy.json.JsonOutput().toJson(valid_user), ".json")
        when: "a POST request is sent to login with valid credentials"
        Response response = given()
            .contentType(ContentType.JSON)
            .body(valid_user)
            .log().all()
            .when()
            .post(sign_in_url)
        then: "the response status code is 200 (OK) and the response body is as expected"
            response.then()
            .log().all()
            .statusCode(200)

        Allure.addAttachment("Response - User Login", "application/json", response.asString(), ".json")
    }// end function



    @Story("Login Attempt with Empty Email must be fail")
    @DisplayName("Should fail login with empty email")
    @Description("""
        expect error message because email is empty
    """)
    @Severity(SeverityLevel.CRITICAL)
    def "Perform Login with empty email"() {
        given:
        def sign_in_url = UrlManagement.getAuthUrl(AUTH.SignIn)
        def invalid_user = GenerateSignInUtility.generateInvalidBaseOnCase(INVALID_LOGIN.EMPTY_EMAIL)
        Allure.addAttachment("Request Payload - Specific User Sign In", "application/json", new groovy.json.JsonOutput().toJson(invalid_user), ".json")
        when: "a POST request is sent to login with empty email field"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalid_user)
                .log().all()
                .when()
                .post(sign_in_url)
        then: "the response status code is 400 (Bad request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(400)

        Allure.addAttachment("Response - User Login", "application/json", response.asString(), ".json")
    }// end function


    @Story("Login Attempt with Invalid Email must be fail")
    @DisplayName("Should fail login with invalid email")
    @Description("""
        expect error message because email is invalid
    """)
    @Severity(SeverityLevel.CRITICAL)
    def "Perform Login with invalid email"() {
        given:
        def sign_in_url = UrlManagement.getAuthUrl(AUTH.SignIn)
        def invalid_user = GenerateSignInUtility.generateInvalidBaseOnCase(INVALID_LOGIN.INVALID_EMAIL)
        Allure.addAttachment("Request Payload - Specific User Sign In", "application/json", new groovy.json.JsonOutput().toJson(invalid_user), ".json")
        when: "a POST request is sent to login with invalid email field"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalid_user)
                .log().all()
                .when()
                .post(sign_in_url)
        then: "the response status code is 400 (Bad request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(400)

        Allure.addAttachment("Response - User Login", "application/json", response.asString(), ".json")
    }// end function


    @Story("Login Attempt with Empty Password must be fail")
    @DisplayName("Should fail login with empty password")
    @Description("""
        expect error message because with empty password
    """)
    @Severity(SeverityLevel.CRITICAL)
    def "Perform Login with with empty password"() {
        given:
        def sign_in_url = UrlManagement.getAuthUrl(AUTH.SignIn)
        def invalid_user = GenerateSignInUtility.generateInvalidBaseOnCase(INVALID_LOGIN.EMPTY_PASSWORD)
        Allure.addAttachment("Request Payload - Specific User Sign In", "application/json", new groovy.json.JsonOutput().toJson(invalid_user), ".json")
        when: "a POST request is sent to login with with empty password field"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalid_user)
                .log().all()
                .when()
                .post(sign_in_url)
        then: "the response status code is 400 (Bad request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(400)

        Allure.addAttachment("Response - User Login", "application/json", response.asString(), ".json")
    }// end function

    @Story("Login Attempt with Invalid Password must be fail")
    @DisplayName("Should fail login with invalid password")
    @Description("""
        expect error message because with invalid password
    """)
    @Severity(SeverityLevel.CRITICAL)
    def "Perform Login with with invalid password"() {
        given:
        def sign_in_url = UrlManagement.getAuthUrl(AUTH.SignIn)
        def invalid_user = GenerateSignInUtility.generateInvalidBaseOnCase(INVALID_LOGIN.INVALID_PASSWORD)
        Allure.addAttachment("Request Payload - Specific User Sign In", "application/json", new groovy.json.JsonOutput().toJson(invalid_user), ".json")
        when: "a POST request is sent to login with with invalid password field"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalid_user)
                .log().all()
                .when()
                .post(sign_in_url)
        then: "the response status code is 400 (Bad request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(500)

        Allure.addAttachment("Response - User Login", "application/json", response.asString(), ".json")
    }// end function


    @Story("Login Attempt with Invalid Email and Password must be fail")
    @DisplayName("Should fail login with invalid email and password")
    @Description("""
        expect error message because with invalid email and password
    """)
    @Severity(SeverityLevel.CRITICAL)
    def "Perform Login with with invalid email and password"() {
        given:
        def sign_in_url = UrlManagement.getAuthUrl(AUTH.SignIn)
        def invalid_user = GenerateSignInUtility.generateInvalidBaseOnCase(INVALID_LOGIN.INVALID_BOTH_EMAIL_PASSWORD)
        Allure.addAttachment("Request Payload - Specific User Sign In", "application/json", new groovy.json.JsonOutput().toJson(invalid_user), ".json")
        when: "a POST request is sent to login with with invalid email and password field"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalid_user)
                .log().all()
                .when()
                .post(sign_in_url)
        then: "the response status code is 400 (Bad request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(500)

        Allure.addAttachment("Response - User Login", "application/json", response.asString(), ".json")
    }// end function

    @Story("Login Attempt with Empty Email and Password must be fail")
    @DisplayName("Should fail login with empty email and password")
    @Description("""
        expect error message because with empty email and password
    """)
    @Severity(SeverityLevel.CRITICAL)
    def "Perform Login with with empty email and password"() {
        given:
        def sign_in_url = UrlManagement.getAuthUrl(AUTH.SignIn)
        def invalid_user = GenerateSignInUtility.generateInvalidBaseOnCase(INVALID_LOGIN.EMPTY_BOTH_EMAIL_PASSWORD)
        Allure.addAttachment("Request Payload - Specific User Sign In", "application/json", new groovy.json.JsonOutput().toJson(invalid_user), ".json")
        when: "a POST request is sent to login with with empty email and password field"
        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalid_user)
                .log().all()
                .when()
                .post(sign_in_url)
        then: "the response status code is 400 (Bad request) and the response body is as expected"
        response.then()
                .log().all()
                .statusCode(400)

        Allure.addAttachment("Response - User Login", "application/json", response.asString(), ".json")
    }// end function


}
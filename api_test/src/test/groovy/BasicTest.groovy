import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Step
import io.qameta.allure.Story
import io.restassured.response.Response
import org.junit.jupiter.api.DisplayName
import spock.lang.Specification
import static io.restassured.RestAssured.*
import static io.restassured.matcher.RestAssuredMatchers.*
import static org.hamcrest.Matchers.*


@Epic("REST API Tests") // Overall epic for these tests
@Feature("Posts Management") // Feature being tested
class BasicTest extends Specification {


    def "Basic Test"() {

        def url = "https://jsonplaceholder.typicode.com/posts"
        given().
            log().all().
        when().
            get("https://jsonplaceholder.typicode.com/todos/1").
        then().
            log().all().
            statusCode(200)
        println "\nFetching all posts and verifying the"

    }
    // Example of how you might use the 'url' variable you had defined
    def "should receive status code 200 when fetching all posts"() {
        given: "the base URL for posts"
        // You can set a baseURI for all requests in this spec or globally
        // RestAssured.baseURI = "https://jsonplaceholder.typicode.com"
        // For a single test, using the full URL is also fine.
        def postsUrl = "https://jsonplaceholder.typicode.com/posts"

        when: "a GET request is made to fetch all posts"
        def response = get(postsUrl)

        then: "the response status code is 200"
        response.then()
                .log().ifValidationFails() // Log only if something goes wrong
                .statusCode(200)
                .body("size()", greaterThan(0)) // Example of an additional check

        and: "a message can be printed"
        println "\nSuccessfully fetched all posts with status code 200"
    }
    // Negative Test: Expecting a 404 Not Found for a non-existent resource
    def "should receive status code 404 when fetching a non-existent post"() {
        given: "the URL for a non-existent post"
        // Assuming /posts/0 or /posts/999999999 would not exist
        def nonExistentPostUrl = "https://jsonplaceholder.typicode.com/posts/0"

        when: "a GET request is made to fetch the non-existent post"
        Response response = get(nonExistentPostUrl)

        then: "the response status code is 404"
        response.then()
                .log().all() // Log details, especially useful for failing tests
                .statusCode(404)

        and: "a message can be printed"
        println "\nCorrectly received 404 for non-existent post: ${nonExistentPostUrl}"
    }

    @Story("Create New Post") // User story or specific scenario
    @DisplayName("Should successfully create a new post and receive status code 201 with correct data")
    @Description("""
        This test verifies that a new post can be created via the API.
        It checks for:
        - Status code 201 (Created)
        - Response body containing the sent data (title, body, userId)
        - A new 'id' assigned to the created post
    """)
    @Severity(SeverityLevel.CRITICAL)
    def "should create a new post and receive status code 201"() {
        // Annotate the block label

        given: "the payload for a new post"
            def newPostPayload = [ // Groovy map will be serialized to JSON
                                   title: 'My Awesome Groovy Post',
                                   body: 'This post was created using REST Assured and Groovy!',
                                   userId: 77
            ]
        // Optional: Set base URI if not set globally
        // RestAssured.baseURI = "https://jsonplaceholder.typicode.com"
        def postsUrl = "https://jsonplaceholder.typicode.com/posts"

        when: "a POST request is made to create a new post"
        Response response = given()
                .contentType("application/json") // Specify the content type of the request body
                .body(newPostPayload)            // Set the request body
                .log().all()                     // Log the request details
                .when()
                .post(postsUrl)                  // Send the POST request

        then: "the response status code is 201 (Created)"
        response.then()
                .log().all()                                 // Log the response details
                .statusCode(201)                             // Assert the status code

        and: "the response body contains the sent data and a new id"
        response.then()
                .body("title", equalTo(newPostPayload.title))
                .body("body", equalTo(newPostPayload.body))
                .body("userId", equalTo(newPostPayload.userId))
                .body("id", notNullValue())                  // Assert that an ID was assigned
                .body("id", instanceOf(Integer.class))       // Assert the ID is an integer

        and: "a message can be printed"
        println "\nSuccessfully created a new post with ID: ${response.path("id")} and status 201"
        println "Created post title: ${response.path("title")}"
    }
}
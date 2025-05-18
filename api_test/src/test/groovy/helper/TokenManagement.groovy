package helper


import helper.user.GenerateSignInUtility
import io.restassured.response.Response
// Unused imports like DisplayName and Specification are removed for clarity
import static io.restassured.RestAssured.*
import io.restassured.http.ContentType
// Unused matcher imports are removed

@Singleton // Apply the Singleton annotation
class TokenManagement {

    // jwt_token becomes an instance variable, private to encourage use of getCurrentToken
    private String jwt_token = null

    /**
     * Initializes the JWT token by signing in if it hasn't been initialized yet.
     * This method is private as it's an internal part of the singleton's state management.
     *
     * @return The fetched JWT token.
     * @throws IllegalStateException if token extraction fails.
     */
    private String initializeTokenAndGet() {
        // This check ensures that if this method were somehow called when token exists,
        // it doesn't re-fetch. However, getCurrentToken is the primary guard.
        if (this.jwt_token != null) {
            println "INFO: Token already initialized. Reusing existing token."
            return this.jwt_token
        }

        println "INFO: Attempting to initialize JWT token..."
        def login_url = UrlManagement.getAuthUrl(AUTH.SignIn)
        def correct_user = GenerateSignInUtility.generateValidUserToSignIn()

        Response response = given()
                .contentType(ContentType.JSON)
                .body(correct_user)
                .log().all() // Logging request for debugging
                .when()
                .post(login_url)
                .then()
                .log().all() // Logging response for debugging
                .statusCode(200) // Assuming 200 is the success status for login
                .extract()
                .response()

        String extractedToken = response.path("data.token")

        if (extractedToken == null || extractedToken.trim().isEmpty()) {
            String errorMessage = "ERROR: Failed to extract token from login response. Response body: ${response.asString()}"
            println errorMessage
            // Throw an exception to indicate a critical failure
            throw new IllegalStateException("Failed to obtain JWT token after login. Ensure the login endpoint is responsive and returns the token correctly.")
        }

        this.jwt_token = extractedToken
        println "INFO: JWT Token initialized successfully."
        return this.jwt_token
    }

    /**
     * Retrieves the current JWT token.
     * If the token has not been initialized yet, this method will trigger the
     * sign-in process to obtain it.
     *
     * @return The JWT token.
     */
    String getCurrentToken() {
        if (this.jwt_token == null) {
            println "WARN: JWT Token has not been initialized yet. Calling token initialization."
            // Call the internal method to fetch and set the token
            initializeTokenAndGet()
        }
        return this.jwt_token
    }

    /**
     * Clears the currently stored JWT token.
     * This can be useful in testing scenarios where you need to force re-authentication.
     */
    void clearToken() {
        println "INFO: Clearing stored JWT token."
        this.jwt_token = null
    }
}
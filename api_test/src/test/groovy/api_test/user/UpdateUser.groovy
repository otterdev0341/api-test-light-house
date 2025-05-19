package api_test.user

import helper.TokenManagement
import helper.UrlManagement
import helper.fetch.FetchApiResponseUtility
import helper.user.GenerateUpdateUserUtility
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

@Epic("Edit User Information") // Overall epic for these tests
@Feature("Perform Update User Information")
class UpdateUser extends Specification {

    @Story("Update User Information - Successfully")
    @DisplayName("Should update user information successfully")
    @Description("""
    Verifies that a user's information can be successfully updated using a valid authentication token and a valid request payload.
    The test expects a 200 (OK) status code, indicating a successful update.
    It also notes that the backend is expected to ignore empty fields in the payload, meaning those specific user attributes will not be modified if their corresponding payload fields are empty.
""")
    @Severity(SeverityLevel.NORMAL)
    def "Should update user information successfully"() {
        def the_token = TokenManagement.instance.getCurrentToken()
        def update_user_url = UrlManagement.getUpdateUserUrl()
        def update_user_body = GenerateUpdateUserUtility.GenerateValidUpdateUser()

        Allure.addAttachment("Request Payload - Update User", "application/json", update_user_body.toString(), ".json")

        Response response = FetchApiResponseUtility.FetUpdateUserWithCredential(
                update_user_url, update_user_body, the_token
        )

        Allure.addAttachment("Response - Update User", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
            status_code == 200

    }


    @Story("Update User Information - With Invalid credential")
    @DisplayName("Try update user with invalid credential")
    @Description("""
    Tests the system's behavior when an attempt is made to update user information using an invalid authentication token.
    The test sends a valid update payload but deliberately corrupts the token.
    It expects a 401 (Unauthorized) status code in response, indicating that the request was rejected due to invalid credentials.
""")
    @Severity(SeverityLevel.NORMAL)
    def "Try update user with invalid credential"() {
        def the_token = TokenManagement.instance.getCurrentToken() + "zx3G"
        def update_user_url = UrlManagement.getUpdateUserUrl()
        def update_user_body = GenerateUpdateUserUtility.GenerateValidUpdateUser()

        Allure.addAttachment("Request Payload - Update User", "application/json", update_user_body.toString(), ".json")
        Response response = FetchApiResponseUtility.FetUpdateUserWithCredential(
                update_user_url, update_user_body, the_token
        )
        def status_code = response.statusCode()

        Allure.addAttachment("Response - Update User", "application/json", response.asString(), ".json")
        expect:
        status_code == 401
    }

    @Story("Update User Information - with all empty field")
    @DisplayName("Try update with all empty field")
    @Description("""
    Verifies the API's handling of an update request where specific fields in the payload (first_name and last_name) are intentionally set to empty strings.
    The test uses a valid authentication token.
    It expects a 200 (OK) status code, implying that the API processes the request successfully and, based on previous test descriptions, likely does not update the fields that were sent as empty.
""")
    @Severity(SeverityLevel.NORMAL)
    def "Try update with all empty field"() {
        def the_token = TokenManagement.instance.getCurrentToken()
        def update_user_url = UrlManagement.getUpdateUserUrl()
        def update_user_body = GenerateUpdateUserUtility.GenerateValidUpdateUser()
        update_user_body.setFirst_name("")
        update_user_body.setLast_name("")

        Allure.addAttachment("Request Payload - Update User", "application/json", update_user_body.toString(), ".json")

        Response response = FetchApiResponseUtility.FetUpdateUserWithCredential(
                update_user_url, update_user_body, the_token
        )

        Allure.addAttachment("Response - Update User", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
        status_code == 200
    }
}
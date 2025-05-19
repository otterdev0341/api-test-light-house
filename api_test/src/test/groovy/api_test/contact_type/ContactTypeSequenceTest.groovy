package api_test.contact_type


import dto.contact_type.ReqUpdateContactTypeDto
import dto.expense_type.ReqCreateExpenseTypeDto
import helper.RandomUtility
import helper.TokenManagement
import helper.UrlManagement
import helper.fetch.FetchApiResponseUtility
import io.qameta.allure.Allure
import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story
import io.restassured.response.Response
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Epic("expense type")
@Feature("expense type Sequence")
@Stepwise
class ContactTypeSequenceTest extends Specification {

    @Shared
    String expense_type_id// Initialized to null by default for String


    @Story("Create new expense type")
    @Description("""
    This test attempts to create a new expense type with a valid name.
    It verifies that the API call is successful (e.g., status code 201)
    and that an ID is returned for the newly created expense type.
    The extracted ID is stored for potential use in subsequent @Stepwise tests.
    """)
    @Severity(SeverityLevel.NORMAL)
    def "create new expense type"() {
        given: "the API endpoint, a new asset type payload, and a JWT token"
        def base_url = UrlManagement.baseExpenseType
        def expense_type_name = "expense_type${RandomUtility.generateRandom7DigitNumber()}"
        def new_expense_type_payload = new ReqCreateExpenseTypeDto(name: expense_type_name)
        def jwt_token = TokenManagement.instance.currentToken // Ensure 'currentToken' is the correct way to get the token

        // Optional: Attach request payload to Allure report
        Allure.addAttachment("Request Payload - Create expense type", "application/json", new_expense_type_payload.toString(), ".json")

        when: "a POST request is sent to create the expense type"
        Response response = FetchApiResponseUtility.FetchCreateWithCredential(base_url, new_expense_type_payload, jwt_token)

        then: "the response details are logged and the ID is extracted"
        println "Response (Pretty Print):"
        response.prettyPrint() // Good for debugging

        // Optional: Attach full response to Allure report
        Allure.addAttachment("API Response - Create expense type", "application/json", response.asString(), ".json")

        def created_id = response.path("data.id")
        if (created_id != null && !created_id.toString().isEmpty()) { // Check for null and non-empty string
            this.expense_type_id = created_id.toString() // Ensure it's a string
            println "Successfully retrieved new expense type ID: ${this.expense_type_id}"
        } else {
            println "Failed to retrieve new expense type ID from response path 'data.id'."
            // You might want to fail the test here if an ID is always expected
            // throw new AssertionError("Failed to retrieve new asset type ID")
        }


        response.statusCode() == 201 // Assuming 201 is the success code for creation
        this.expense_type_id != null    // Assert that an ID was actually captured
        !this.expense_type_id.isEmpty() // Assert that the captured ID is not empty
        expense_type_name == response.path("data.name")
        // You could also assert directly on the response path if you don't need to store the ID for @Stepwise
        // response.path("data.id") != null
    }


    @Story("Get expense type by ID")
    @Description("Verifies that a previously created expense type can be fetched using its ID.") // Added description
    @Severity(SeverityLevel.NORMAL)
    def "get expense type by id"() {
        given: "a valid expense_type_id from the previous step and a JWT token"
        Allure.step("Attempting to fetch expense type with ID: '${this.expense_type_id}'")
        println "Value of this.expense_type_id at the start of 'get expense type by id': '${this.expense_type_id}'"

        // CRITICAL: Check if expense_type_id is valid before making the call
        if (this.expense_type_id == null || this.expense_type_id.trim().isEmpty()) {
            throw new IllegalStateException("Cannot proceed to get expense type by ID: asset_type_id is null or empty. Previous step likely failed to set it. Value: '${this.expense_type_id}'")
        }

        def get_url = UrlManagement.baseExpenseType // This is likely just the base path, e.g., "/v1/asset-type"
        // The FetchGetByIdWithCredential should append the ID.
        def jwt_token = TokenManagement.instance.currentToken

        when: "a GET request is sent to retrieve the expense type using the stored ID"
        // Assuming FetchGetByIdWithCredential constructs the URL like: get_url + "/" + assetIdToFetch
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(get_url, this.expense_type_id, jwt_token)
        Allure.step("Received response for get expense type by ID. Status: ${response.statusCode()}")


        then: "the response is logged and relevant data is extracted"
        println "Get expense type By ID - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense type By ID", "application/json", response.asString(), ".json")

        def response_status = response.getStatusCode()
        def id_retrieved = response.path("data.id") // Assuming the GET response also has data.id
        Allure.step("Retrieved ID from GET response: '$id_retrieved'")


        expect: "the retrieval was successful and the correct expense type is returned"
        response_status == 200
        id_retrieved != null
        id_retrieved.toString() == this.expense_type_id
    }

    @Story("Get all expense type")
    @Description("Get all expense type, then check is the new one that created exist")
    @Severity(SeverityLevel.NORMAL)
    def "get all expense type"() {
        given: "a valid expense_type_id from the previous step and a JWT token"
        Allure.step("Attempting to fetch expense type with ID: '${this.expense_type_id}'")
        println "Value of this.expense_type_id at the start of 'get expense type by id': '${this.expense_type_id}'"

        // CRITICAL: Check if expense_type_id is valid before making the call
        if (this.expense_type_id == null || this.expense_type_id.trim().isEmpty()) {
            throw new IllegalStateException("Cannot proceed to get expense type by ID: asset_type_id is null or empty. Previous step likely failed to set it. Value: '${this.expense_type_id}'")
        }

        def get_url = UrlManagement.baseExpenseType // This is likely just the base path, e.g., "/v1/asset-type"
        // The FetchGetByIdWithCredential should append the ID.
        def jwt_token = TokenManagement.instance.currentToken

        when: "a GET request is sent to retrieve the expense type using the stored ID"
        // Assuming FetchGetByIdWithCredential constructs the URL like: get_url + "/" + assetIdToFetch
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(get_url, jwt_token)
        Allure.step("Received response for get expense type by ID. Status: ${response.statusCode()}")


        then: "the response is logged and relevant data is extracted"
        println "Get expense type By ID - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense type By ID", "application/json", response.asString(), ".json")

        def response_status = response.getStatusCode()
        def total_data_object= response.path("data.length") // Assuming the GET response also has data.id

        // Fix the find query - use proper GPath expression with proper string comparison
        def foundContactType = response.path("data.data.find { it.id == '${this.expense_type_id}' }")
        println "Found asset type: ${foundContactType}"


        expect: "the retrieval was successful and the correct asset type is returned"
        response_status == 200
        total_data_object != 0

        foundContactType.id == this.expense_type_id

    }

    @Story("update expense type")
    @Description("use id that create in 1st step and update name, then check is the new one that created exist and name changed")
    @Severity(SeverityLevel.NORMAL)
    def "update expense type"() {
        given: "dto to update, jwt and url to perform update"
        Allure.step("Attempting to fetch expense type with ID: '${this.expense_type_id}'")
        def update_name = "updated_expense_${RandomUtility.generateRandom7DigitString()}"
        def update_contact_type_payload = new ReqUpdateContactTypeDto(name: update_name)
        def jwt_token = TokenManagement.instance.currentToken
        def update_url = UrlManagement.baseExpenseType
        when: "a PUT request is sent to update the expense type"
        Response response = FetchApiResponseUtility.FetchUpdateWithCredential(update_url, update_contact_type_payload, jwt_token, this.expense_type_id)
        Allure.step("Received response for get expense type by ID. Status: ${response.statusCode()}")
        then: "the response is logged and relevant data is extracted"
        println "Get expense type after updated - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense type By ID", "application/json", response.asString(), ".json")

        def updated_name_from_response = response.path("data.name")
        def status_code = response.statusCode()

        expect:
        status_code == 200
        updated_name_from_response == update_name
    }

    @Story("delete expense type")
    @Description("use id that create in 1st step and delete")
    @Severity(SeverityLevel.NORMAL)
    def "delete expense type"() {
        given: "url, token and id"
        def delete_url = UrlManagement.baseExpenseType
        def jwt_token = TokenManagement.instance.currentToken
        when: "a DELETE request is sent to delete the expense type"
        Response response = FetchApiResponseUtility.FetchDeleteWithCredential(delete_url, jwt_token, this.expense_type_id)
        then: "the response is logged and relevant data is extracted"
        println "Delete expense type - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Delete expense type", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
        status_code == 200
    }

    @Story("try to get expense type after deleted")
    @Description("try to get expense type after deleted")
    @Severity(SeverityLevel.NORMAL)
    def "find the expense type after deleted"() {
        given: "url, token and id"
        def url = UrlManagement.baseExpenseType
        def jwt_token = TokenManagement.instance.currentToken
        when: "a GET request is sent to delete the expense type"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(url, this.expense_type_id, jwt_token)
        then: "the response is logged and relevant data is extracted"
        println "Get expense type after deleted - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense type By ID", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
        status_code == 404

    }


}
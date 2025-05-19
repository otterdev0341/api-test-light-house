package api_test.expense_type

import dto.contact_type.ReqCreateContactTypeDto
import dto.contact_type.ReqUpdateContactTypeDto
import helper.RandomUtility
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

@Epic("Expense Type")
@Feature("Expense Type 404")
@Stepwise
class ExpenseType404Test extends Specification {
    @Shared
    String expense_type_id// Initialized to null by default for String


    @Story("Create new expense type win invalid token")
    @Description("""
    Try to create new expense type with invalid token, expect 401 response
    """)
    @Severity(SeverityLevel.CRITICAL)
    def "create new expense type"() {
        given: "the API endpoint, a new expense type payload, and a JWT token"
        def base_url = UrlManagement.baseExpenseType
        def new_asset_type_payload = new ReqCreateContactTypeDto(name: "test_expense_type_${RandomUtility.generateRandom7DigitNumber()}")
        def jwt_token = ""

        // Optional: Attach request payload to Allure report
        Allure.addAttachment("Request Payload - Create expense type", "application/json", new_asset_type_payload.toString(), ".json")

        when: "a POST request is sent to create the expense type"
        Response response = FetchApiResponseUtility.FetchCreateWithCredential(base_url, new_asset_type_payload, jwt_token)

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
            // throw new AssertionError("Failed to retrieve new expense type ID")
        }


        response.statusCode() == 401 // Assuming 201 is the success code for creation


        // You could also assert directly on the response path if you don't need to store the ID for @Stepwise
        // response.path("data.id") != null
    }


    @Story("Get expense type by ID with invalid token")
    @Description("Verifies this feature will not allow access with invalid token") // Added description
    @Severity(SeverityLevel.CRITICAL)
    def "get expense type by id"() {
        given: "a valid asset_type_id from the previous step and a JWT token"
        Allure.step("Attempting to fetch expense type with ID: '${this.expense_type_id}'")
        println "Value of this.asset_type_id at the start of 'get expense type by id': '${this.expense_type_id}'"

        // CRITICAL: Check if asset_type_id is valid before making the call
        if (this.expense_type_id == null || this.expense_type_id.trim().isEmpty()) {
            throw new IllegalStateException("Cannot proceed to get expense type by ID: asset_type_id is null or empty. Previous step likely failed to set it. Value: '${this.expense_type_id}'")
        }

        def get_url = UrlManagement.baseExpenseType // This is likely just the base path, e.g., "/v1/asset-type"
        // The FetchGetByIdWithCredential should append the ID.
        def jwt_token = ""

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
        response_status == 401

    }

    @Story("Get all expense type with invalid token")
    @Description("check get all expense type, will not allow access with invalid token ")
    @Severity(SeverityLevel.CRITICAL)
    def "get all expense type"() {
        given: "a valid asset_type_id from the previous step and a JWT token"
        Allure.step("Attempting to fetch expense type with ID: '${this.expense_type_id}'")
        println "Value of this.asset_type_id at the start of 'get expense type by id': '${this.expense_type_id}'"




        def get_url = UrlManagement.baseExpenseType // This is likely just the base path, e.g., "/v1/asset-type"
        // The FetchGetByIdWithCredential should append the ID.
        def jwt_token = ""

        when: "a GET request is sent to retrieve the expense type using the stored ID"
        // Assuming FetchGetByIdWithCredential constructs the URL like: get_url + "/" + assetIdToFetch
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(get_url, jwt_token)
        Allure.step("Received response for get expense type by ID. Status: ${response.statusCode()}")


        then: "the response is logged and relevant data is extracted"
        println "Get expense type By ID - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense type By ID", "application/json", response.asString(), ".json")

        def response_status = response.getStatusCode()


        // Fix the find query - use proper GPath expression with proper string comparison
        def foundAssetType = response.path("data.data.find { it.id == '${this.expense_type_id}' }")
        println "Found expense type: ${foundAssetType}"


        expect: "the retrieval was successful and the correct expense type is returned"
        response_status == 401


    }

    @Story("update expense type with invalid token")
    @Description("check is update expense type will not allow access with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    def "update expense type"() {
        given: "dto to update, jwt and url to perform update"
        Allure.step("Attempting to fetch expense type with ID: '${this.expense_type_id}'")
        def update_name = "updated_${RandomUtility.generateRandom7DigitString()}"
        def update_asset_type_payload = new ReqUpdateContactTypeDto(name: update_name)
        def jwt_token = ""
        def update_url = UrlManagement.baseExpenseType
        when: "a PUT request is sent to update the expense type"
        Response response = FetchApiResponseUtility.FetchUpdateWithCredential(update_url, update_asset_type_payload, jwt_token, this.expense_type_id)
        Allure.step("Received response for get expense type by ID. Status: ${response.statusCode()}")
        then: "the response is logged and relevant data is extracted"
        println "Get expense type after updated - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense type By ID", "application/json", response.asString(), ".json")


        def status_code = response.statusCode()

        expect:
        status_code == 401

    }

    @Story("delete expense type with invalid token")
    @Description("check is delete expense type will not allow access with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    def "delete expense type"() {
        given: "url, token and id"
        def delete_url = UrlManagement.baseExpenseType
        def jwt_token = ""
        when: "a DELETE request is sent to delete the expense type"
        Response response = FetchApiResponseUtility.FetchDeleteWithCredential(delete_url, jwt_token, this.expense_type_id)
        then: "the response is logged and relevant data is extracted"
        println "Delete expense type - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Delete expense type", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
        status_code == 401
    }
}
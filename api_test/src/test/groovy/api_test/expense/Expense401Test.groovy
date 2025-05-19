package api_test.expense

import api_test.utility.HelperGetExpenseTypeId
import dto.expense.ReqCreateExpenseDto
import dto.expense.ReqUpdateExpenseDto
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


@Epic("Expense")
@Feature("Expense401")
@Stepwise
class Expense401Test extends Specification {

    @Shared
    String new_expense_id// Initialized to null by default for String


    @Story("Create new expense win invalid token")
    @Description("""
    Try to create new expense with invalid token, expect 401 response
    """)
    @Severity(SeverityLevel.CRITICAL)
    def "create new expense"() {
        given: "the API endpoint, a new expense payload, and a JWT token"
        def base_url = UrlManagement.baseExpense
        def new_expense_payload = new ReqCreateExpenseDto(
                description: "test_banking${RandomUtility.generateRandom7DigitNumber()}",
                expense_type_id: "${HelperGetExpenseTypeId.firstExpenseTypeDetail[0]}"
        )
        def jwt_token = ""

        // Optional: Attach request payload to Allure report
        Allure.addAttachment("Request Payload - Create Expense", "application/json", new_expense_payload.toString(), ".json")

        when: "a POST request is sent to create the expense"
        Response response = FetchApiResponseUtility.FetchCreateWithCredential(base_url, new_expense_payload, jwt_token)

        then: "the response details are logged and the ID is extracted"
        println "Response (Pretty Print):"
        response.prettyPrint() // Good for debugging

        // Optional: Attach full response to Allure report
        Allure.addAttachment("API Response - Create Expense", "application/json", response.asString(), ".json")

        def created_id = response.path("data.id")
        if (created_id != null && !created_id.toString().isEmpty()) { // Check for null and non-empty string
            this.new_expense_id = created_id.toString() // Ensure it's a string
            println "Successfully retrieved new expense ID: ${this.new_expense_id}"
        } else {
            println "Failed to retrieve new expense ID from response path 'data.id'."
            // You might want to fail the test here if an ID is always expected
            // throw new AssertionError("Failed to retrieve new expense ID")
        }


        response.statusCode() == 401 // Assuming 201 is the success code for creation


        // You could also assert directly on the response path if you don't need to store the ID for @Stepwise
        // response.path("data.id") != null
    }


    @Story("Get expense by ID with invalid token")
    @Description("Verifies this feature will not allow access with invalid token") // Added description
    @Severity(SeverityLevel.CRITICAL)
    def "get expense by id"() {
        given: "a valid asset_type_id from the previous step and a JWT token"
        Allure.step("Attempting to fetch expense with ID: '${this.new_expense_id}'")
        println "Value of this.asset_type_id at the start of 'get expense by id': '${this.new_expense_id}'"

        // CRITICAL: Check if asset_type_id is valid before making the call
        if (this.new_expense_id == null || this.new_expense_id.trim().isEmpty()) {
            throw new IllegalStateException("Cannot proceed to get expense by ID: asset_type_id is null or empty. Previous step likely failed to set it. Value: '${this.new_expense_id}'")
        }

        def get_url = UrlManagement.baseExpense // This is likely just the base path, e.g., "/v1/asset-type"
        // The FetchGetByIdWithCredential should append the ID.
        def jwt_token = ""

        when: "a GET request is sent to retrieve the expense using the stored ID"
        // Assuming FetchGetByIdWithCredential constructs the URL like: get_url + "/" + assetIdToFetch
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(get_url, this.new_expense_id, jwt_token)
        Allure.step("Received response for get expense by ID. Status: ${response.statusCode()}")


        then: "the response is logged and relevant data is extracted"
        println "Get expense By ID - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense By ID", "application/json", response.asString(), ".json")

        def response_status = response.getStatusCode()
        def id_retrieved = response.path("data.id") // Assuming the GET response also has data.id
        Allure.step("Retrieved ID from GET response: '$id_retrieved'")


        expect: "the retrieval was successful and the correct expense is returned"
        response_status == 401

    }

    @Story("Get all expense with invalid token")
    @Description("check get all expense, will not allow access with invalid token ")
    @Severity(SeverityLevel.CRITICAL)
    def "get all expense"() {
        given: "a valid asset_type_id from the previous step and a JWT token"
        Allure.step("Attempting to fetch expense with ID: '${this.new_expense_id}'")
        println "Value of this.asset_type_id at the start of 'get expense by id': '${this.new_expense_id}'"




        def get_url = UrlManagement.baseExpense // This is likely just the base path, e.g., "/v1/asset-type"
        // The FetchGetByIdWithCredential should append the ID.
        def jwt_token = ""

        when: "a GET request is sent to retrieve the expense using the stored ID"
        // Assuming FetchGetByIdWithCredential constructs the URL like: get_url + "/" + assetIdToFetch
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(get_url, jwt_token)
        Allure.step("Received response for get expense by ID. Status: ${response.statusCode()}")


        then: "the response is logged and relevant data is extracted"
        println "Get expense By ID - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense By ID", "application/json", response.asString(), ".json")

        def response_status = response.getStatusCode()


        // Fix the find query - use proper GPath expression with proper string comparison
        def foundAssetType = response.path("data.data.find { it.id == '${this.new_expense_id}' }")
        println "Found expense: ${foundAssetType}"


        expect: "the retrieval was successful and the correct expense is returned"
        response_status == 401


    }

    @Story("update expense with invalid token")
    @Description("check is update expense will not allow access with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    def "update expense"() {
        given: "dto to update, jwt and url to perform update"
        Allure.step("Attempting to fetch expense with ID: '${this.new_expense_id}'")
        def update_name = "updated_${RandomUtility.generateRandom7DigitString()}"
        def update_asset_type_payload = new ReqUpdateExpenseDto(
                description: update_name,
                expense_type_id: "${HelperGetExpenseTypeId.secondExpenseTypeDetail[0]}"
        )
        def jwt_token = ""
        def update_url = UrlManagement.baseExpense
        when: "a PUT request is sent to update the expense"
        Response response = FetchApiResponseUtility.FetchUpdateWithCredential(update_url, update_asset_type_payload, jwt_token, this.new_expense_id)
        Allure.step("Received response for get expense by ID. Status: ${response.statusCode()}")
        then: "the response is logged and relevant data is extracted"
        println "Get expense after updated - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense By ID", "application/json", response.asString(), ".json")


        def status_code = response.statusCode()

        expect:
        status_code == 401

    }

    @Story("delete expense with invalid token")
    @Description("check is delete expense will not allow access with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    def "delete expense"() {
        given: "url, token and id"
        def delete_url = UrlManagement.baseExpense
        def jwt_token = ""
        when: "a DELETE request is sent to delete the expense"
        Response response = FetchApiResponseUtility.FetchDeleteWithCredential(delete_url, jwt_token, this.new_expense_id)
        then: "the response is logged and relevant data is extracted"
        println "Delete expense - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Delete expense", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
        status_code == 401
    }
}
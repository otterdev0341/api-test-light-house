package api_test.expense

import api_test.utility.HelperGetContactTypeId
import api_test.utility.HelperGetExpenseTypeId
import dto.expense.ReqCreateExpenseDto
import dto.expense.ReqUpdateExpenseDto
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

@Epic("Expense")
@Feature("Expense Sequence")
@Stepwise
class ExpenseSequenceTest extends Specification {
    @Shared
    String new_expense_id


    @Story("Create new expense")
    @Description("""
        this tes will create new expense with all invalid field,
        then will set it to new_expense_id if create success (201),
        then will use that new_expense_id in other test
""")
    @Severity(SeverityLevel.NORMAL)
    def "create new expense"() {
        given: "api end point, new expense payload, jwt token"
        
        def random_description = "this expense about ${RandomUtility.generateRandom7DigitNumber()}"
        def prepared_expense_type_id = HelperGetExpenseTypeId.firstExpenseTypeDetail[0]
        def end_point = UrlManagement.baseExpense
        
        def expense_payload = new ReqCreateExpenseDto(
                description: random_description,
                expense_type_id: prepared_expense_type_id
        )
        def token = TokenManagement.instance.currentToken
        Allure.addAttachment("Request Payload - Create expense Type", "application/json", expense_payload.toString(), ".json")

        when: "do Post request to create expense"
        Response response = FetchApiResponseUtility.FetchCreateWithCredential(end_point, expense_payload, token)

        then: "the response details are logged and the ID is extracted"
        println "Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Create expense Type", "application/json", response.asString(), ".json")

        def created_id = response.path("data.id")
        if (created_id != null && !created_id.toString().isEmpty()) { // Check for null and non-empty string
            this.new_expense_id = created_id.toString() // Ensure it's a string
            println "Successfully retrieved new expense type ID: ${this.new_expense_id}"
        } else {
            println "Failed to retrieve new expense type ID from response path 'data.id'."
            // You might want to fail the test here if an ID is always expected
            // throw new AssertionError("Failed to retrieve new expense type ID")
        }

        response.statusCode() == 201 // Assuming 201 is the success code for creation
        this.new_expense_id != null    // Assert that an ID was actually captured
        !this.new_expense_id.isEmpty() // Assert that the captured ID is not empty

    }// end create new expense


    @Story("Get expense by ID")
    @Description("Verifies that a previously create expense can be retrieved by its ID")
    @Severity(SeverityLevel.NORMAL)
    def "get expense by id"() {

        given: "a valid_expense_Id from previous step, token"
        Allure.step("Attempting to fetch expense type with ID: '${this.new_expense_id}'")
        if (this.new_expense_id == null || this.new_expense_id.trim().isEmpty()) {
            throw new IllegalStateException("Cannot proceed to get the expense id, bec the id from prev step is null or empty")
        }
        def valid_expense_Id = this.new_expense_id
        def token = TokenManagement.instance.currentToken
        def url = UrlManagement.baseExpense

        when: "a GET request is send to fetch expense"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(url, valid_expense_Id,token)
        Allure.step("Received response for get expense type by ID. Status: ${response.statusCode()}")

        then: " the response is logged and relevant data is extracted"
        println "Get expense by id - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense Type By ID", "application/json", response.asString(), ".json")

        def response_status = response.getStatusCode()
        def id_retrieved = response.path("data.id")
        Allure.step("Retrieved ID from GET reponse: '${id_retrieved}'")

        expect: "all information retrieved successfully"
        response_status == 200
        id_retrieved != null
        id_retrieved.toString() == this.new_expense_id


    }// enc get by id


    @Story("Get all expense type")
    @Description("Verifies that a previously create expense can be retrieved by its ID")
    @Severity(SeverityLevel.NORMAL)
    def "get all expense"() {
        given: "a valid expense_id from the previous step and a JWT token"
        Allure.step("Attempting to fetch expense with ID: '${this.new_expense_id}'")
        println "Value of this.expense_id at the start of 'get expense by id': '${this.new_expense_id}'"

        // CRITICAL: Check if expense is valid before making the call
        if (this.new_expense_id == null || this.new_expense_id.trim().isEmpty()) {
            throw new IllegalStateException("Cannot proceed to get expense by ID: new_expense_id is null or empty. Previous step likely failed to set it. Value: '${this.expense_type_id}'")
        }

        def get_url = UrlManagement.baseExpense // This is likely just the base path, e.g., "/v1/expense-type"
        // The FetchGetByIdWithCredential should append the ID.
        def jwt_token = TokenManagement.instance.currentToken

        when: "a GET request is sent to retrieve the expense type using the stored ID"
        // Assuming FetchGetByIdWithCredential constructs the URL like: get_url + "/" + expenseIdToFetch
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(get_url, jwt_token)
        Allure.step("Received response for get expense by ID. Status: ${response.statusCode()}")


        then: "the response is logged and relevant data is extracted"
        println "Get expense By ID - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense By ID", "application/json", response.asString(), ".json")

        def response_status = response.getStatusCode()
        def total_data_object= response.path("data.length") // Assuming the GET response also has data.id

        // Fix the find query - use proper GPath expression with proper string comparison
        def found_expense = response.path("data.data.find { it.id == '${this.new_expense_id}' }")
        println "Found expense type: ${found_expense}"


        expect: "the retrieval was successful and the correct expense is returned"
        response_status == 200
        total_data_object != 0
        found_expense.id == this.new_expense_id
    }// get all expense



    @Story("update expense")
    @Description("Verifies that a previously create expense can be retrieved by its ID")
    @Severity(SeverityLevel.NORMAL)
    def "update expense"() {
        given: "dto to update, jwt and url to perform update"
        Allure.step("Attempting to update expense with ID: '${this.new_expense_id}'")

        def update_description = "this expense updated about ${RandomUtility.generateRandom7DigitNumber()}"

        def update_expense_id = HelperGetExpenseTypeId.secondExpenseTypeDetail
        def expense_id_to_persist = update_expense_id[0]
        def expense_name_to_validate = update_expense_id[1]

        def update_expense_payload = new ReqUpdateExpenseDto(
                description: update_description,
                expense_type_id: expense_id_to_persist
        )
        def jwt_token = TokenManagement.instance.currentToken
        def update_url = UrlManagement.baseExpense
        when: "a PUT request is sent to update the expense type"
        Response response = FetchApiResponseUtility.FetchUpdateWithCredential(update_url, update_expense_payload, jwt_token, this.new_expense_id)
        Allure.step("Received response for get expense by ID. Status: ${response.statusCode()}")
        then: "the response is logged and relevant data is extracted"
        println "Get expense Type after updated - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense Type By ID", "application/json", response.asString(), ".json")

        def extract_id = response.path("data.id")
        def extract_description = response.path("data.description")
        def extract_expense_type_name = response.path("data.expense_type_name")
        def status_code = response.statusCode()

        expect:
        status_code == 200
        extract_id == this.new_expense_id
        update_description == extract_description
        expense_name_to_validate == extract_expense_type_name
    }// update



    @Story("delete expense")
    @Description("Verifies that a previously create expense can be retrieved by its ID then delete it")
    @Severity(SeverityLevel.NORMAL)
    def "delete expense"() {
        given: "url, token and id"
        def delete_url = UrlManagement.baseExpense
        def jwt_token = TokenManagement.instance.currentToken
        when: "a DELETE request is sent to delete the expense type"
        Response response = FetchApiResponseUtility.FetchDeleteWithCredential(delete_url, jwt_token, this.new_expense_id)
        then: "the response is logged and relevant data is extracted"
        println "Delete expense Type - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Delete expense", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
        status_code == 200
    }// delete expense

    @Story("try to get expense after deleted")
    @Description("try to get expense after deleted")
    @Severity(SeverityLevel.NORMAL)
    def "find the expense after deleted"() {
        given: "url, token and id"
        def url = UrlManagement.baseExpense
        def jwt_token = TokenManagement.instance.currentToken
        when: "a GET request is sent to find the expense type"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(url, this.new_expense_id, jwt_token)
        then: "the response is logged and relevant data is extracted"
        println "Get expense after send DELETE request - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get expense By ID", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
        status_code == 404

    }
}
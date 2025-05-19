package api_test.contact

import api_test.utility.HelperGetAssetTypeId
import api_test.utility.HelperGetContactTypeId
import dto.contact.ReqCreateContactDto
import dto.contact.ReqUpdateContactDto
import helper.RandomUtility
import helper.TokenManagement
import helper.UrlManagement
import helper.fetch.FetchApiResponseUtility
import io.qameta.allure.Allure
import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story
import io.restassured.response.Response
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Epic("Contact")
@Epic("Contact Operation Test")
@Stepwise

class ContactSequenceTest extends Specification {

    @Shared
    String new_contact_id


    @Story("Create new contact")
    @Description("""
        this tes will create new contact with all invalid field,
        then will set it to new_contact_id if create success (201),
        then will use that new_contact_id in other test
""")
    @Severity(SeverityLevel.NORMAL)
    def "create new contact"() {
        given: "api end point, new contact payload, jwt token"

        def prepared_name = "test_contact${RandomUtility.generateRandom7DigitString()}"
        def prepared_business_name = "test_business_name${RandomUtility.generateRandom4AlphabetString()}"
        def random_phone = "${RandomUtility.generateRandom7DigitNumber()}"
        def random_description = "this contact about ${RandomUtility.generateRandom7DigitNumber()}"
        def prepared_contact_type_id = HelperGetContactTypeId.firstContactTypeDetail[0]
        def end_point = UrlManagement.baseContact
        def contact_payload = new ReqCreateContactDto(
                name: prepared_name,
                business_name: prepared_business_name,
                phone: random_phone,
                description: random_description,
                contact_type_id: prepared_contact_type_id
        )
        def token = TokenManagement.instance.currentToken
        Allure.addAttachment("Request Payload - Create contact Type", "application/json", contact_payload.toString(), ".json")

        when: "do Post request to create contact"
        Response response = FetchApiResponseUtility.FetchCreateWithCredential(end_point, contact_payload, token)

        then: "the response details are logged and the ID is extracted"
        println "Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Create contact Type", "application/json", response.asString(), ".json")

        def created_id = response.path("data.id")
        if (created_id != null && !created_id.toString().isEmpty()) { // Check for null and non-empty string
            this.new_contact_id = created_id.toString() // Ensure it's a string
            println "Successfully retrieved new contact type ID: ${this.new_contact_id}"
        } else {
            println "Failed to retrieve new contact type ID from response path 'data.id'."
            // You might want to fail the test here if an ID is always expected
            // throw new AssertionError("Failed to retrieve new contact type ID")
        }

        response.statusCode() == 201 // Assuming 201 is the success code for creation
        this.new_contact_id != null    // Assert that an ID was actually captured
        !this.new_contact_id.isEmpty() // Assert that the captured ID is not empty

    }// end create new contact


    @Story("Get contact by ID")
    @Description("Verifies that a previously create contact can be retrieved by its ID")
    @Severity(SeverityLevel.NORMAL)
    def "get contact by id"() {

        given: "a valid_contact_Id from previous step, token"
        Allure.step("Attempting to fetch contact type with ID: '${this.new_contact_id}'")
        if (this.new_contact_id == null || this.new_contact_id.trim().isEmpty()) {
            throw new IllegalStateException("Cannot proceed to get the contact id, bec the id from prev step is null or empty")
        }
        def valid_contact_Id = this.new_contact_id
        def token = TokenManagement.instance.currentToken
        def url = UrlManagement.baseContact

        when: "a GET request is send to fetch contact"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(url, valid_contact_Id,token)
        Allure.step("Received response for get contact type by ID. Status: ${response.statusCode()}")

        then: " the response is logged and relevant data is extracted"
        println "Get contact by id - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get contact Type By ID", "application/json", response.asString(), ".json")

        def response_status = response.getStatusCode()
        def id_retrieved = response.path("data.id")
        Allure.step("Retrieved ID from GET reponse: '${id_retrieved}'")

        expect: "all information retrieved successfully"
        response_status == 200
        id_retrieved != null
        id_retrieved.toString() == this.new_contact_id


    }// enc get by id


    @Story("Get all contact type")
    @Description("Verifies that a previously create contact can be retrieved by its ID")
    @Severity(SeverityLevel.NORMAL)
    def "get all contact"() {
        given: "a valid contact_id from the previous step and a JWT token"
        Allure.step("Attempting to fetch contact with ID: '${this.new_contact_id}'")
        println "Value of this.contact_id at the start of 'get contact by id': '${this.new_contact_id}'"

        // CRITICAL: Check if contact is valid before making the call
        if (this.new_contact_id == null || this.new_contact_id.trim().isEmpty()) {
            throw new IllegalStateException("Cannot proceed to get contact by ID: new_contact_id is null or empty. Previous step likely failed to set it. Value: '${this.contact_type_id}'")
        }

        def get_url = UrlManagement.baseContact // This is likely just the base path, e.g., "/v1/contact-type"
        // The FetchGetByIdWithCredential should append the ID.
        def jwt_token = TokenManagement.instance.currentToken

        when: "a GET request is sent to retrieve the contact type using the stored ID"
        // Assuming FetchGetByIdWithCredential constructs the URL like: get_url + "/" + contactIdToFetch
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(get_url, jwt_token)
        Allure.step("Received response for get contact by ID. Status: ${response.statusCode()}")


        then: "the response is logged and relevant data is extracted"
        println "Get contact By ID - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get contact By ID", "application/json", response.asString(), ".json")

        def response_status = response.getStatusCode()
        def total_data_object= response.path("data.length") // Assuming the GET response also has data.id

        // Fix the find query - use proper GPath expression with proper string comparison
        def foundcontactType = response.path("data.data.find { it.id == '${this.new_contact_id}' }")
        println "Found contact type: ${foundcontactType}"


        expect: "the retrieval was successful and the correct contact is returned"
        response_status == 200
        total_data_object != 0
        foundcontactType.id == this.new_contact_id
    }// get all contact



    @Story("update contact")
    @Description("Verifies that a previously create contact can be retrieved by its ID")
    @Severity(SeverityLevel.NORMAL)
    def "update contact"() {
        given: "dto to update, jwt and url to perform update"
        Allure.step("Attempting to update contact with ID: '${this.new_contact_id}'")
        def update_name = "updated_${RandomUtility.generateRandom7DigitString()}"
        def update_business_name = "updated_bizz_${RandomUtility.generateRandom4AlphabetString()}"
        def update_phone = "${RandomUtility.generateRandom7DigitNumber()}"
        def update_description = "this contact updated about ${RandomUtility.generateRandom7DigitNumber()}"

        def update_contact_id = HelperGetContactTypeId.secondContactTypeDetail;

        def contact_id_to_persist = update_contact_id[0]
        def contact_name_to_validate = update_contact_id[1]

        def update_contact_payload = new ReqUpdateContactDto(
                name: update_name,
                business_name: update_business_name,
                phone: update_phone,
                description: update_description,
                contact_type_id: contact_id_to_persist
        )
        def jwt_token = TokenManagement.instance.currentToken
        def update_url = UrlManagement.baseContact
        when: "a PUT request is sent to update the contact type"
        Response response = FetchApiResponseUtility.FetchUpdateWithCredential(update_url, update_contact_payload, jwt_token, this.new_contact_id)
        Allure.step("Received response for get contact by ID. Status: ${response.statusCode()}")
        then: "the response is logged and relevant data is extracted"
        println "Get contact Type after updated - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get contact Type By ID", "application/json", response.asString(), ".json")

        def extract_id = response.path("data.id")
        def extract_name = response.path("data.name")
        def extract_business_name = response.path("data.business_name")
        def extract_phone = response.path("data.phone")
        def extract_description = response.path("data.description")
        def extract_contact_type_name = response.path("data.contact_type_name")

        def status_code = response.statusCode()

        expect:
        status_code == 200
        extract_id == this.new_contact_id
        update_name == extract_name
        update_business_name == extract_business_name
        update_phone == extract_phone
        update_description == extract_description
        contact_name_to_validate == extract_contact_type_name
    }// update



    @Story("delete contact")
    @Description("Verifies that a previously create contact can be retrieved by its ID then delete it")
    @Severity(SeverityLevel.NORMAL)
    def "delete contact"() {
        given: "url, token and id"
        def delete_url = UrlManagement.baseContact
        def jwt_token = TokenManagement.instance.currentToken
        when: "a DELETE request is sent to delete the contact type"
        Response response = FetchApiResponseUtility.FetchDeleteWithCredential(delete_url, jwt_token, this.new_contact_id)
        then: "the response is logged and relevant data is extracted"
        println "Delete contact Type - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Delete contact", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
        status_code == 200
    }// delete contact

    @Story("try to get contact after deleted")
    @Description("try to get contact after deleted")
    @Severity(SeverityLevel.NORMAL)
    def "find the contact after deleted"() {
        given: "url, token and id"
        def url = UrlManagement.baseContact
        def jwt_token = TokenManagement.instance.currentToken
        when: "a GET request is sent to find the contact type"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(url, this.new_contact_id, jwt_token)
        then: "the response is logged and relevant data is extracted"
        println "Get contact after send DELETE request - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get contact By ID", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
        status_code == 404

    }

}
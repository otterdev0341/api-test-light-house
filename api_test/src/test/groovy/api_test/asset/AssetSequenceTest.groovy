package api_test.asset

import api_test.utility.HelperGetAssetTypeId
import dto.asset.ReqCreateAssetDto
import dto.asset.ReqUpdateAssetDto
import dto.asset_type.ReqUpdateAssetTypeDto
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


@Epic("Asset")
@Feature("Asset Operation")
@Stepwise
class AssetSequenceTest extends Specification {

    @Shared
    String new_asset_id


    @Story("Create new asset")
    @Description("""
        this tes will create new asset with all invalid field,
        then will set it to new_asset_id if create success (201),
        then will use that new_asset_id in other test
""")
    @Severity(SeverityLevel.NORMAL)
    def "create new asset"() {
        given: "api end point, new asset payload, jwt token"
        def end_point = UrlManagement.baseAssetUrl
        def asset_payload = new ReqCreateAssetDto(
                name: "test_${RandomUtility.generateRandom7DigitString()}",
                asset_type_id: HelperGetAssetTypeId.validAssetTypeId
        )
        def token = TokenManagement.instance.currentToken
        Allure.addAttachment("Request Payload - Create Asset Type", "application/json", asset_payload.toString(), ".json")

        when: "do Post request to create asset"
        Response response = FetchApiResponseUtility.FetchCreateWithCredential(end_point, asset_payload, token)

        then: "the response details are logged and the ID is extracted"
        println "Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Create Asset Type", "application/json", response.asString(), ".json")

        def created_id = response.path("data.id")
        if (created_id != null && !created_id.toString().isEmpty()) { // Check for null and non-empty string
            this.new_asset_id = created_id.toString() // Ensure it's a string
            println "Successfully retrieved new asset type ID: ${this.new_asset_id}"
        } else {
            println "Failed to retrieve new asset type ID from response path 'data.id'."
            // You might want to fail the test here if an ID is always expected
            // throw new AssertionError("Failed to retrieve new asset type ID")
        }

        response.statusCode() == 201 // Assuming 201 is the success code for creation
        this.new_asset_id != null    // Assert that an ID was actually captured
        !this.new_asset_id.isEmpty() // Assert that the captured ID is not empty

    }// end create new asset


    @Story("Get asset by ID")
    @Description("Verifies that a previously create asset can be retrieved by its ID")
    @Severity(SeverityLevel.NORMAL)
    def "get asset by id"() {

        given: "a valid_asset_Id from previous step, token"
        Allure.step("Attempting to fetch asset type with ID: '${this.new_asset_id}'")
        if (this.new_asset_id == null || this.new_asset_id.trim().isEmpty()) {
            throw new IllegalStateException("Cannot proceed to get the asset id, bec the id from prev step is null or empty")
        }
        def valid_asset_Id = this.new_asset_id
        def token = TokenManagement.instance.currentToken
        def url = UrlManagement.baseAssetUrl

        when: "a GET request is send to fetch asset"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(url, valid_asset_Id,token)
        Allure.step("Received response for get asset type by ID. Status: ${response.statusCode()}")

        then: " the response is logged and relevant data is extracted"
        println "Get asset by id - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get Asset Type By ID", "application/json", response.asString(), ".json")

        def response_status = response.getStatusCode()
        def id_retrieved = response.path("data.id")
        Allure.step("Retrieved ID from GET reponse: '${id_retrieved}'")

        expect: "all information retrieved successfully"
        response_status == 200
        id_retrieved != null
        id_retrieved.toString() == this.new_asset_id


    }// enc get by id


    @Story("Get all asset type")
    @Description("Verifies that a previously create asset can be retrieved by its ID")
    @Severity(SeverityLevel.NORMAL)
    def "get all asset"() {
        given: "a valid asset_id from the previous step and a JWT token"
        Allure.step("Attempting to fetch asset with ID: '${this.new_asset_id}'")
        println "Value of this.asset_id at the start of 'get asset by id': '${this.new_asset_id}'"

        // CRITICAL: Check if asset is valid before making the call
        if (this.new_asset_id == null || this.new_asset_id.trim().isEmpty()) {
            throw new IllegalStateException("Cannot proceed to get asset by ID: new_asset_id is null or empty. Previous step likely failed to set it. Value: '${this.asset_type_id}'")
        }

        def get_url = UrlManagement.baseAssetUrl // This is likely just the base path, e.g., "/v1/asset-type"
        // The FetchGetByIdWithCredential should append the ID.
        def jwt_token = TokenManagement.instance.currentToken

        when: "a GET request is sent to retrieve the asset type using the stored ID"
        // Assuming FetchGetByIdWithCredential constructs the URL like: get_url + "/" + assetIdToFetch
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(get_url, jwt_token)
        Allure.step("Received response for get asset by ID. Status: ${response.statusCode()}")


        then: "the response is logged and relevant data is extracted"
        println "Get Asset By ID - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get Asset By ID", "application/json", response.asString(), ".json")

        def response_status = response.getStatusCode()
        def total_data_object= response.path("data.length") // Assuming the GET response also has data.id

        // Fix the find query - use proper GPath expression with proper string comparison
        def foundAssetType = response.path("data.data.find { it.id == '${this.new_asset_id}' }")
        println "Found asset type: ${foundAssetType}"


        expect: "the retrieval was successful and the correct asset is returned"
        response_status == 200
        total_data_object != 0
        foundAssetType.id == this.new_asset_id
    }// get all asset



    @Story("update asset")
    @Description("Verifies that a previously create asset can be retrieved by its ID")
    @Severity(SeverityLevel.NORMAL)
    def "update asset"() {
        given: "dto to update, jwt and url to perform update"
        Allure.step("Attempting to update asset with ID: '${this.new_asset_id}'")
        def update_name = "updated_${RandomUtility.generateRandom7DigitString()}"
        def update_asset_id = HelperGetAssetTypeId.valid2ndAssetTypeId
        def update_asset_payload = new ReqUpdateAssetDto(
                name: update_name,
                asset_type_id: update_asset_id
        )
        def jwt_token = TokenManagement.instance.currentToken
        def update_url = UrlManagement.baseAssetUrl
        when: "a PUT request is sent to update the asset type"
        Response response = FetchApiResponseUtility.FetchUpdateWithCredential(update_url, update_asset_payload, jwt_token, this.new_asset_id)
        Allure.step("Received response for get asset by ID. Status: ${response.statusCode()}")
        then: "the response is logged and relevant data is extracted"
        println "Get Asset Type after updated - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get Asset Type By ID", "application/json", response.asString(), ".json")

        def updated_name_from_response = response.path("data.name")
        def updated_asset_type = response.path("data.asset_type")
        def status_code = response.statusCode()

        expect:
        status_code == 200
        updated_name_from_response == update_name

    }// update



    @Story("delete asset")
    @Description("Verifies that a previously create asset can be retrieved by its ID then delete it")
    @Severity(SeverityLevel.NORMAL)
    def "delete asset"() {
        given: "url, token and id"
        def delete_url = UrlManagement.baseAssetUrl
        def jwt_token = TokenManagement.instance.currentToken
        when: "a DELETE request is sent to delete the asset type"
        Response response = FetchApiResponseUtility.FetchDeleteWithCredential(delete_url, jwt_token, this.new_asset_id)
        then: "the response is logged and relevant data is extracted"
        println "Delete Asset Type - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Delete Asset", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
        status_code == 200
    }// delete asset

    @Story("try to get asset after deleted")
    @Description("try to get asset after deleted")
    @Severity(SeverityLevel.NORMAL)
    def "find the asset after deleted"() {
        given: "url, token and id"
        def url = UrlManagement.baseAssetUrl
        def jwt_token = TokenManagement.instance.currentToken
        when: "a GET request is sent to find the asset type"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(url, this.new_asset_id, jwt_token)
        then: "the response is logged and relevant data is extracted"
        println "Get Asset after send DELETE request - Response (Pretty Print):"
        response.prettyPrint()
        Allure.addAttachment("API Response - Get Asset By ID", "application/json", response.asString(), ".json")
        def status_code = response.statusCode()
        expect:
        status_code == 404

    }


}// end test
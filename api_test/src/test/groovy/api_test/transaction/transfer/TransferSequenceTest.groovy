package api_test.transaction.transfer

import api_test.utility.HelperGetAsset
import api_test.utility.HelperGetContactId
import api_test.utility.HelperGetTransactionType
import api_test.utility.TRANSACTION_TYPE
import dto.transaction.tranfer.ReqCreateTransferDto
import dto.transaction.tranfer.ReqUpdateTransferDto
import dto.transaction.tranfer.ResEntryTransferDto
import helper.RandomUtility
import helper.TokenManagement
import helper.UrlManagement
import helper.fetch.FetchApiResponseUtility
import helper.fetch.FetchCurrentSheetUtility
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

import java.math.RoundingMode


@Epic("TransferSequenceTest")
@Feature("Create, GetBy, GetAll, Update, Delete")
@Stepwise
class TransferSequenceTest extends Specification {

    @Shared
    String new_transfer_transaction

    @Shared
    String new_amount_on_create

    @Shared
    String latest_source_asset_id

    @Shared
    String latest_source_asset_name

    @Shared
    String latest_destination_asset_id

    @Shared
    String latest_destination_asset_name

    @Shared
    String latest_amount


    @Story("use create transfer transaction")
    @Description("""
        create transfer record then check the current balance of source and destination,
        the source destination should decrease and destination asset must be increase
        Example:
        asset_id 1, cash amount 500
        asset_id 2, bank account 7732 amount 0
        transfer asset_id 1 [50] to asset_id 2 
        expect: current of asset_id 1 is [450], current of asset_id 2 is [50]
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Record Transfer"() {
        given: "url, token, payload, current balance of source asset and destination asset"
        // prepare data
        def base_url = UrlManagement.transferRecord
        def jwt_token = TokenManagement.instance.currentToken
        def transaction_type_id = HelperGetTransactionType.get_transaction_type(TRANSACTION_TYPE.TRANSFER)
        def amount_to_transfer = 342.23
        this.new_amount_on_create = amount_to_transfer
        def target_source_asset = HelperGetAsset.get_asset_dto()
        def source_asset = target_source_asset.id
        def target_destination_asset = HelperGetAsset.get_asset_dto_for_update()
        def target_contact = HelperGetContactId.get_first_contact_detail()
        def target_contact_id = target_contact.id
        def note = "test transfer${RandomUtility.generateRandom7DigitNumber()}"
        //create payload
        def transfer_payload = new ReqCreateTransferDto(
                transaction_type_id: transaction_type_id.id,
                amount: amount_to_transfer,
                asset_id: source_asset,
                destination_asset_id: target_destination_asset.id,
                contact_id: target_contact_id,
                note: note
        )

        when: "create transfer record, and get current balance of source asset and destination asset"
        // get current balance of source asset and destination asset before create transfer
        def current_balance_of_source_asset_before_response = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(target_source_asset.id, target_source_asset.name)
        def current_balance_of_destination_asset_before_response = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(target_destination_asset.id, target_destination_asset.name)
        // POST method to create transfer with the payload
        Response create_transfer_response = FetchApiResponseUtility.FetchCreateWithCredential(base_url, transfer_payload, jwt_token)
        // get current balance of source asset and destination asset after create transfer
        def current_balance_of_source_asset_after_response = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(target_source_asset.id, target_source_asset.name)
        def current_balance_of_destination_asset_after_response = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(target_destination_asset.id, target_destination_asset.name)
        this.new_transfer_transaction = create_transfer_response.path("data.id")
        then: "extract the value for prepare validation"
        // current of source and destination
        def extract_current_source_asset = current_balance_of_source_asset_before_response.balance
        def extract_current_destination_asset = current_balance_of_destination_asset_before_response.balance

        def extract_performed_source_asset = current_balance_of_source_asset_after_response.balance
        def extract_performed_destination_asset = current_balance_of_destination_asset_after_response.balance

        // cast to avoid decimal precision
        BigDecimal current_source_asset = new BigDecimal(extract_current_source_asset.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal current_destination_asset = new BigDecimal(extract_current_destination_asset.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal performed_source_asset = new BigDecimal(extract_performed_source_asset.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal performed_destination_asset = new BigDecimal(extract_performed_destination_asset.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal transfer_unit = new BigDecimal(amount_to_transfer.toString()).setScale(2, RoundingMode.HALF_UP)

        // extract created response for validation
        def success_response = new ResEntryTransferDto(
                id: create_transfer_response.path("data.id"),
                transaction_type_name: create_transfer_response.path("data.transaction_type_name"),
                amount: create_transfer_response.path("data.amount"),
                asset_name: create_transfer_response.path("data.asset_name"),
                destination_asset_name: create_transfer_response.path("data.destination_asset_name"),
                contact_name: create_transfer_response.path("data.contact_name"),
                note: create_transfer_response.path("data.note"),
                created_at: create_transfer_response.path("data.created_at"),
                updated_at: create_transfer_response.path("data.updated_at")
        )

        expect: "all validation must be pass"
        // must be success
        create_transfer_response.getStatusCode() == 201

        // transfer validation
        current_source_asset.subtract(transfer_unit) == performed_source_asset
        current_destination_asset.add(transfer_unit) == performed_destination_asset

        // created response validation
        success_response.id != null
        success_response.transaction_type_name == "transfer"
        BigDecimal success_amount = new BigDecimal(success_response.amount.toString()).setScale(2, RoundingMode.HALF_UP)
        success_amount == transfer_unit
        success_response.asset_name == target_source_asset.name
        success_response.destination_asset_name == target_destination_asset.name
        success_response.contact_name == target_contact.name
        success_response.note == note

        // set new it for other test

    }// record transfer




    @Story("User want to see detail of transfer transaction")
    @Description("""
        after transfer record created, the use might want to see the detail of the transfer,
        It should be exist and retrieved properly after created successfully in previous funtion
""")
    @Severity(SeverityLevel.NORMAL)
    def "view transfer"() {

        given: "target id to perform GET, token, url"
        def transfer_transaction_id = this.new_transfer_transaction
        def jwt_token = TokenManagement.instance.currentToken
        def base_url = UrlManagement.transferRecord
        Allure.addAttachment("Request path param - Get Income Record", "application/json", transfer_transaction_id.toString(), ".json")
        when: "GET to retrieve transfer record"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(base_url, transfer_transaction_id, jwt_token)
        Allure.addAttachment("Response body - Get Transfer Record", "application/json", response.path("data").toString(), ".json")

        then: "extract the value from response to perform validation"
        def extract_id = response.path("data.id")

        expect: "all validation must be pass"
        extract_id == transfer_transaction_id
        response.getStatusCode() == 200
    } // view transform record


    @Story("view all transfer record")
    @Description("""
        Get all transfer record then check the on that create in previous test
        it should be exist with response status 200
""")
    @Severity(SeverityLevel.NORMAL)
    def "view all the transfer"() {

        given: "url, token"
        Allure.step("1 of 4 : prepare url and jwt_token to perform GET")
        def base_url = UrlManagement.transferRecord
        def jwt_token = TokenManagement.instance.currentToken


        when: "Send GET to retrieve all transfer record"
        Allure.step("2 of 4 : Send GET to retrieve all transfer record")
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(base_url, jwt_token)
        Allure.addAttachment("Response body - Get transfer Record", "application/json", response.path("data").toString(), ".json")


        then: "extract id to validate"
        Allure.step("3 of 4 : Extract all information from response")
        def extract_response = response.getStatusCode()
        def target_length = response.path("data.length")
        def target_validate = response.path("data.data.find { it.id == '${this.new_transfer_transaction}'}")

        Allure.step("4 of 4 : Perform Validate, to check the response is correct or not")
        extract_response == 200
        target_length > 0
        target_validate != null


    }// view all payment



    @Story("User want to change information of transfer that has created")
    @Description("""
        After created, the use might want to change the information
    """)
    @Severity(SeverityLevel.NORMAL)
    def "update transfer"() {

        given: "payload to perform PUT, url, token"
        def base_url = UrlManagement.transferRecord
        def jwt_token = TokenManagement.instance.currentToken

        BigDecimal amount = new BigDecimal(123.33).setScale(2, RoundingMode.HALF_UP)
        def source_asset = HelperGetAsset.get_asset_dto_for_update()
        def destination_asset = HelperGetAsset.get_asset_dto()
        def target_contact = HelperGetContactId.get_first_contact_detail_for_update()
        def updated_note = "from update test ${RandomUtility.generateRandom4AlphabetString()} meow"

        // created payload
        def updated_payload = new ReqUpdateTransferDto(
            amount:                 amount,
            asset_id:               source_asset.id,
            destination_asset_id:   destination_asset.id,
            contact_id:             target_contact.id,
            note:                   updated_note
        )
        // set variable for delete test
        this.latest_amount = amount
        this.latest_destination_asset_id = destination_asset.id
        this.latest_destination_asset_name = destination_asset.name
        this.latest_source_asset_id = source_asset.id
        this.latest_source_asset_name = source_asset.name


        when: "retrieved current balance of source and asset id, before, then perform PUT, then retrieved current of source and destination again "
        // fetch current before
        def current_source_before_performed = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(source_asset.id, source_asset.name)
        def current_destination_before_performed = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(destination_asset.id, destination_asset.name)
        // wait to perform
        Thread.sleep(3000)
        // perform update
        Response update_response = FetchApiResponseUtility.FetchUpdateWithCredential(base_url, updated_payload, jwt_token, this.new_transfer_transaction)
        // fet current after
        Thread.sleep(4000)
        def current_source_after_performed = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(source_asset.id, source_asset.name)
        def current_destination_after_performed = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(destination_asset.id, destination_asset.name)
        then: "extract the value prepare for validation"

        BigDecimal current_source_before = new BigDecimal(current_source_before_performed.balance).setScale(2, RoundingMode.HALF_UP)
        BigDecimal current_destination_before = new BigDecimal(current_destination_before_performed.balance).setScale(2, RoundingMode.HALF_UP)
        BigDecimal current_source_after = new BigDecimal(current_source_after_performed.balance).setScale(2, RoundingMode.HALF_UP)
        BigDecimal current_destination_after = new BigDecimal(current_destination_after_performed.balance).setScale(2, RoundingMode.HALF_UP)
        BigDecimal amount_of_first_transfer = new BigDecimal(this.new_amount_on_create.toString()).setScale(2, RoundingMode.HALF_UP)

        // convert updated_response into dto
        def success_response = new ResEntryTransferDto(
                id: update_response.path("data.id"),
                transaction_type_name: update_response.path("data.transaction_type_name"),
                amount: update_response.path("data.amount"),
                asset_name: update_response.path("data.asset_name"),
                destination_asset_name: update_response.path("data.destination_asset_name"),
                contact_name: update_response.path("data.contact_name"),
                note: update_response.path("data.note"),
                created_at: update_response.path("data.created_at"),
                updated_at: update_response.path("data.updated_at")
        )
        BigDecimal success_response_amount = new BigDecimal(success_response.amount.toString()).setScale(2, RoundingMode.HALF_UP)
        expect: "all validation must pass"
        // 1. Response status validation
        update_response.getStatusCode() == 200

        // 2. Response data validation
        success_response.id == this.new_transfer_transaction
        success_response.transaction_type_name == "transfer"
        success_response_amount == amount
        success_response.asset_name == source_asset.name
        success_response.destination_asset_name == destination_asset.name  // Fixed: was source_asset.name
        success_response.contact_name == target_contact.name
        success_response.note == updated_note

        // 3. current balance validation
//        current_source_before.subtract(amount) == current_source_after
//        current_destination_before.add(amount) == current_destination_after

        // 4. Additional validations
        success_response.created_at != null
        success_response.updated_at != null
        success_response.updated_at != success_response.created_at  // Should be different after update



    }// update transfer


    @Story("delete transfer record")
    @Feature("delete transfer")
    @Description("""
        after create and update transfer Record in previous function,
        now use that id to delete it
""")
    def "delete transfer"() {
        given: "url, token"
        Allure.step("Prepare url, token, to perform DELETE")
        def base_url = UrlManagement.transferRecord
        def jwt_token = TokenManagement.instance.currentToken
        def target_id = this.new_transfer_transaction
        Allure.addAttachment("Request param - to perform Delete", "application/json", "${target_id}", ".json")

        when: "get current, and current after delete, and send DELETE method"

        def source_before_delete = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(this.latest_source_asset_id, this.latest_source_asset_name)
        def destination_before_delete = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(this.latest_destination_asset_id, this.latest_destination_asset_name)
        Response delete_response = FetchApiResponseUtility.FetchDeleteWithCredential(base_url, jwt_token, target_id)
        Thread.sleep(5000)
        def source_after_delete = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(this.latest_source_asset_id, this.latest_source_asset_name)
        def destination_after_delete = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(this.latest_destination_asset_id, this.latest_destination_asset_name)

        then: "extract the value to perform validation"
        // extract and prepare
        // source transfer to destination
        BigDecimal source_before = new BigDecimal(source_before_delete.balance.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal destination_before = new BigDecimal(destination_before_delete.balance.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal source_after = new BigDecimal(source_after_delete.balance.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal destination_after = new BigDecimal(destination_after_delete.balance.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal latest_updated_amount = new BigDecimal(this.latest_amount.toString()).setScale(2, RoundingMode.HALF_UP)

        expect: "all validation must be pass"
        delete_response.getStatusCode() == 200
        source_before.add(latest_updated_amount) == source_after
        destination_before.subtract(latest_updated_amount) == destination_after

    }// delete


    @Story("get transfer that deleted")
    @Feature("get transfer that deleted")
    @Description("""
        after delete transfer in previous function, now check it is exist or not
""")
    def "get transfer with the id that deleted"() {

        given: "url, token, target id"
        def base_url = UrlManagement.transferRecord
        def jwt_token = TokenManagement.instance.currentToken
        def target_id = this.new_transfer_transaction

        when: "call GET method"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(base_url, target_id, jwt_token)
        Allure.addAttachment("Response Body - from GET transfer that deleted with id ${target_id}", "application/json", response.toString(), ".json")

        then: "validate the response"
        response.getStatusCode() == 404
    }





}// end class


//class ReqUpdateTransferDto {
//    Double amount
//    String asset_id
//    String destination_asset_id
//    String contact_id
//    String note
//}


//@ToString
//class ResEntryTransferDto {
//    String id
//    String transaction_type_name
//    String amount
//    String asset_name
//    String destination_asset_name
//    String contact_name
//    String note
//    String created_at
//    String updated_at
//}

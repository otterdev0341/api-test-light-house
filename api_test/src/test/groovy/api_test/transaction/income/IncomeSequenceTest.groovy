package api_test.transaction.income

import dto.transaction.income.ReqUpdateIncomeDto

import java.math.RoundingMode
import api_test.utility.HelperGetAsset
import api_test.utility.HelperGetContactId
import api_test.utility.HelperGetTransactionType
import api_test.utility.TRANSACTION_TYPE
import dto.transaction.income.ReqCreateIncomeDto
import dto.transaction.income.ResEntryIncomeDto
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

@Epic("IncomeSequenceTest")
@Feature("Create, GetBy, GetAll, Update, Delete")
@Stepwise
class IncomeSequenceTest extends Specification {


//    class ReqCreateIncomeDto {
//        String transaction_type_id // income, transfer, expense,
//        Double amount
//        String asset_id
//        String contact_id
//        String note
//    }
    @Shared
    String new_income_transaction

    @Story("create income record")
    @Description("""
        create the income record then check the current_balance of that asset before and after,
        it must update correct
        example: 
        asset_id : 123x before update amount is 100.00
        create income record for asset_id 123x with 25.00
        asset_id: 123x after update amount is 125.00
""")
    @Severity(SeverityLevel.NORMAL)
    def "Record the income"() {
        given: "create payload, token, url, current_balance"
        def base_url = UrlManagement.incomeRecord
        def jwt_token = TokenManagement.instance.currentToken
            Allure.step("1 of 5 : prepared transaction type id for assign to payload")
        def income_transaction_type = HelperGetTransactionType.get_transaction_type(TRANSACTION_TYPE.INCOME)
            Allure.step("2 of 5 : prepared amount to persist for assign to payload")
        def amount_to_create = 27.32
            Allure.step("3 of 5 : prepare asset to assign to payload")
        def target_asset = HelperGetAsset.get_asset_dto()
        def asset_id = target_asset.id
            Allure.step("4 of 5 : prepare contact id for assign to payload")
        def target_contact = HelperGetContactId.get_first_contact_detail()
        def contact_id = target_contact.id
            Allure.step("5 of 5 : prepare note for assign to payload")
        def note = "test note${RandomUtility.generateRandom7DigitNumber()}"
        def create_income_payload = new ReqCreateIncomeDto(
                transaction_type_id: income_transaction_type.id,
                amount: amount_to_create,
                asset_id: asset_id,
                contact_id: contact_id,
                note: note

        )
            Allure.addAttachment("Request Payload - Create Income Record", "application/json", create_income_payload.toString(), ".json")


        when: "Get to retrieve current balance, and POST to create income record "
            Allure.step("1 of 3 : Retrived lasted current balance of asset id ${asset_id} to verify")
        def before_current_sheet = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(asset_id,target_asset.name)
            Allure.step("2 of 3 : Send POST to create income record")
        Response response = FetchApiResponseUtility.FetchCreateWithCredential(base_url, create_income_payload, jwt_token)
        def response_payload = response.path("data")
            Allure.addAttachment("Response Payload - Create Income Record", "application/json", response_payload.toString(), ".json")
            Allure.step("3 of 3 : Retrived lasted current balance of asset id ${asset_id} to verify")
        sleep(7000) // 7 seconds to let operation success
        def after_current_sheet = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(asset_id,target_asset.name)
        then: "extract the value from response to perform validation"
        // extraction for response validation
        def extract_response = response.getStatusCode()
        def extract_amount = response.path("data.amount")
        def extract_asset_name = response.path("data.asset_name")
        def extract_contact_name = response.path("data.contact_name")
        def extract_id = response.path("data.id")
        def extract_note = response.path("data.note")
        def extract_transaction_type_name = response.path("data.transaction_type_name")



        // response validation
        extract_response == 201
        extract_asset_name == target_asset.name
        extract_contact_name == target_contact.name
        extract_id != null
        extract_note == note
        extract_transaction_type_name == income_transaction_type.name
        // validation current sheet
        // validation current sheet
        BigDecimal before = new BigDecimal(before_current_sheet.balance.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal amount = new BigDecimal(amount_to_create.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal after = new BigDecimal(after_current_sheet.balance.toString()).setScale(2, RoundingMode.HALF_UP)

        assert after == before.add(amount).setScale(2, RoundingMode.HALF_UP)

        // set for other test
        if (extract_id != null) {
            this.new_income_transaction = extract_id
        }

    }// create test


    @Story("view the income")
    @Description("""
        Get the income record after created in previous test,
        It should be exist with response status 200
""")
    @Severity(SeverityLevel.NORMAL)
    def "View the income"() {

        given: "target id to perform GET, token, url"
        def transaction_id = this.new_income_transaction
        def base_url = UrlManagement.incomeRecord
        def jwt_token = TokenManagement.instance.currentToken
        Allure.addAttachment("Request path param - Get Income Record", "application/json", transaction_id.toString(), ".json")

        when: "Get to retrieve income record"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(base_url, transaction_id, jwt_token)
        Allure.addAttachment("Response body - Get Income Record", "application/json", response.toString(), ".json")

        then: "extract the value from response to perform validation"
        def extract_status = response.getStatusCode()
        def extract_id = response.path("data.id")

        extract_status == 200
        extract_id == transaction_id
    }// view the income


    @Story("view all income")
    @Description("""
        Get all income record then check the on that create in previous test
        it should be exist with response status 200
""")
    @Severity(SeverityLevel.NORMAL)
    def "view all the income"() {

        given: "url, token"
        Allure.step("1 of 4 : prepare url and jwt_token to perform GET")
        def base_url = UrlManagement.incomeRecord
        def jwt_token = TokenManagement.instance.currentToken


        when: "Send GET to retrieve all income record"
        Allure.step("2 of 4 : Send GET to retrieve all income record")
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(base_url, jwt_token)
        Allure.addAttachment("Response body - Get All Income Record", "application/json", response.toString(), ".json")


        then: "extract id to validate"
        Allure.step("3 of 4 : Extract all information from response")
        def extract_response = response.getStatusCode()
        def target_length = response.path("data.length")
        def target_validate = response.path("data.data.find { it.id == '${this.new_income_transaction}'}")

        Allure.step("4 of 4 : Perform Validate, to check the response is correct or not")
        extract_response == 200
        target_length > 0
        target_validate != null


    }


    @Story("update income")
    @Feature("update income")
    @Description("""
        After success create new income record now try to update it,
        Also validate It before update and after update, is it return correct value
""")
    def "update income"() {
        given: "payload to perform PUT, url, token"
        Allure.step("Prepare payload to perform PUT, url, token")
        def base_url = UrlManagement.incomeRecord
        def jwt_token = TokenManagement.instance.currentToken
        def asset_to_update = HelperGetAsset.get_asset_dto_for_update()
        def contact_to_update = HelperGetContactId.get_first_contact_detail_for_update()
        def note_to_update = "test update note${RandomUtility.generateRandom7DigitNumber()}"
        def update_payload = new ReqUpdateIncomeDto(
                amount: 99.99,
                asset_id: asset_to_update.id,
                contact_id: contact_to_update.id,
                note: note_to_update
        )

        Allure.addAttachment("Request body - Update Income", "application/json", update_payload.toString(), ".json")

        when: "GET and transform to ResEntryIncomeDto to check the value before update"
        Response income_get_response = FetchApiResponseUtility.FetchGetByIdWithCredential(base_url, this.new_income_transaction, jwt_token)
        def current_value = new ResEntryIncomeDto(
                id: income_get_response.path("data.id"),
                transaction_type_name: income_get_response.path("data.transaction_type_name"),
                amount: income_get_response.path("data.amount"),
                asset_name: income_get_response.path("data.asset_name"),
                contact_name: income_get_response.path("data.contact_name"),
                note: income_get_response.path("data.note"),
        )
        Allure.addAttachment("Response body - of Income id ${this.new_income_transaction} before perform PUT to update", "application/json", current_value.toString(), ".json")

        then: "PUT with the payload and convert response into ResEntryIncomeDto to validate"
        Response income_put_response = FetchApiResponseUtility.FetchUpdateWithCredential(base_url, update_payload, jwt_token, this.new_income_transaction)
        def updated_value = new ResEntryIncomeDto(
                id: income_put_response.path("data.id"),
                transaction_type_name: income_put_response.path("data.transaction_type_name"),
                amount: income_put_response.path("data.amount"),
                asset_name: income_put_response.path("data.asset_name"),
                contact_name: income_put_response.path("data.contact_name"),
                note: income_put_response.path("data.note"),
        )
        Allure.addAttachment("Response body - of Income id ${this.new_income_transaction} after perform PUT to update", "application/json", updated_value.toString(), ".json")

        BigDecimal payload = new BigDecimal(update_payload.amount.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal updated_response = new BigDecimal(updated_value.amount.toString()).setScale(2, RoundingMode.HALF_UP)



        expect: "all validation pass"
        Allure.step("start : validate get current_income is correct response")
        income_get_response.getStatusCode() == 200
        income_put_response.getStatusCode() == 200

        Allure.step("start : validate get current_income mut not equal update_value")
        current_value.id == updated_value.id
        current_value.transaction_type_name == updated_value.transaction_type_name
        current_value.asset_name != updated_value.asset_name
        current_value.contact_name != updated_value.contact_name
        current_value.note != updated_value.note


        Allure.step("start: validate the value got from put is correct as in payload to perform update")
        payload == updated_response
        updated_value.note == update_payload.note

    }// end update


    @Story("delete income")
    @Feature("delete income")
    @Description("""
        after create and update Income Record in previous funtion,
        now use that id to delete it
""")
    def "delete income"() {
        given: "url, token"
        def base_url = UrlManagement.incomeRecord
        def jwt_token = TokenManagement.instance.currentToken
        def target_id = this.new_income_transaction
        Allure.addAttachment("Request param - to perform Delete", "application/json", "${target_id}", ".json")

        when: "send DELETE to delete income record"

        Response response = FetchApiResponseUtility.FetchDeleteWithCredential(base_url, jwt_token, target_id)
        Allure.addAttachment("Response Body - from perform Delete", "application/json", response.toString(), ".json")

        then: "perform validation "
        response.getStatusCode() == 200

    }



    @Story("get income that deleted")
    @Feature("get income that deleted")
    @Description("""
        after delete income in previous function, now check it is exist or not
""")
    def "get income with the id that deleted"() {

        given: "url, token, target id"
        def base_url = UrlManagement.incomeRecord
        def jwt_token = TokenManagement.instance.currentToken
        def target_id = this.new_income_transaction

        when: "call GET method"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(base_url, target_id, jwt_token)
        Allure.addAttachment("Response Body - from GET income that deleted with id ${target_id}", "application/json", response.toString(), ".json")

        then: "validate the response"
        response.getStatusCode() == 404
    }





}// end class


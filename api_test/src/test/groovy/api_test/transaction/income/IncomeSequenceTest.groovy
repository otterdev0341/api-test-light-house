package api_test.transaction.income

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
    def "View the income"() {

        given: "target id to perform GET, token, url"
        def transaction_id = this.new_income_transaction
        def base_url = UrlManagement.incomeRecord
        def jwt_token = TokenManagement.instance.currentToken

        when: "Get to retrieve income record"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(base_url, transaction_id, jwt_token)
        then: "extract the value from response to perform validation"
        def extract_status = response.getStatusCode()
        def extract_id = response.path("data.id")

        extract_status == 200
        extract_id == transaction_id
    }// view the income






}// end class


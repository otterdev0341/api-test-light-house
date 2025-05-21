package api_test.transaction.payment

import api_test.utility.HelperGetAsset
import api_test.utility.HelperGetContactId
import api_test.utility.HelperGetExpense
import api_test.utility.HelperGetTransactionType
import api_test.utility.TRANSACTION_TYPE
import dto.payment.ReqCreatePaymentDto
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

@Epic("PaymentSequenceTest")
@Feature("Create, GetBy, GetAll, Update, Delete")
@Stepwise
class PaymentSequenceTest extends Specification {

    @Shared
    String new_payment_transaction


    @Story("the use create payment transaction")
    @Description("""
        create payment record then check the current_balance of that asset id,
        get value both current value and value after create payment.
        example: 
        asset_id : 123x before update amount is 100.00
        create payment record for asset_id 123x with 25.00
        asset_id: 123x after update amount is 75.00
        
    """)
    @Severity(SeverityLevel.NORMAL)
    def "Record the Payment"() {
        given: " create payload, token, url, current_balance"
        def base_url = UrlManagement.paymentRecord
        def jwt_token = TokenManagement.instance.currentToken
            Allure.step("1 of 6 : prepared transaction type id for assign to payload")
        def payment_transaction_type_id = HelperGetTransactionType.get_transaction_type(TRANSACTION_TYPE.PAYMENT)
            Allure.step("2 of 6 : prepared amount to persist for assign to payload")
        def amount_to_pay = 23.43
            Allure.step("3 of 6 : prepare expense to assign to payload")
        def target_expense = HelperGetExpense.get_expense_dto_for_create()
        def expense_id = target_expense.id
            Allure.step("4 of 6 : prepare contact id for assign to payload")
        def target_contact = HelperGetContactId.get_first_contact_detail()
        def contact_id = target_contact.id
            Allure.step("5 of 6 : prepare note for assign to payload")
        def note = "test tis payment note${RandomUtility.generateRandom7DigitNumber()}"
            Allure.step("6 of 6 : prepare asset id for assign to payload")
        def target_asset = HelperGetAsset.get_asset_dto()
        def asset_id = target_asset.id
        def create_payment_payload = new ReqCreatePaymentDto(
                transaction_type_id:        payment_transaction_type_id.id,
                amount:                     amount_to_pay,
                expense_id:                 expense_id,
                contact_id:                 contact_id,
                asset_id:                   asset_id,
                note:                       note
        )
            Allure.addAttachment("Request Payload - Create Payment Record", "application/json", create_payment_payload.toString(), ".json")

        when: "Get to retrieve current balance, and POST to create payment record"
            Allure.step("1 of 3 : Retrived lasted current balance of asset id ${asset_id} to verify")
        def before_current_sheet = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(asset_id,target_asset.name)
            Allure.step("2 of 3 : Send POST to create Payment record")
            Allure.addAttachment("Response current balance - before create payment record", "application/json", before_current_sheet.balance.toString(), ".json")

        Response response = FetchApiResponseUtility.FetchCreateWithCredential(base_url, create_payment_payload, jwt_token)
        def response_payload = response.path("data")
            Allure.addAttachment("Response Payload - Create Payment Record", "application/json", response_payload.toString(), ".json")
            Allure.step("3 of 3 : Retrived lasted current balance of asset id ${asset_id} to verify")
        sleep(3000) // 7 seconds to let operation success
        def after_current_sheet = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(asset_id,target_asset.name)
            Allure.addAttachment("Response Payload - Create Payment Record", "application/json", response.path("data").toString(), ".json")
            Allure.addAttachment("Response current balance - after create payment record with ${amount_to_pay}", "application/json", after_current_sheet.balance.toString(), ".json")
        then: "extract the value from response to perform validation"
        // extract response
        def extract_response = response.getStatusCode()
        def extract_amount = response.path("data.amount")
        def extract_asset_name = response.path("data.asset_name")
        def extract_contact_name = response.path("data.contact_name")
        def extract_expense_name = response.path("data.expense_name")
        def extract_note = response.path("data.note")
        def extract_transaction_type_name = response.path("data.transaction_type_name")
        def extract_id = response.path("data.id")

        // extract current sheet
        BigDecimal before = new BigDecimal(before_current_sheet.balance.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal amount = new BigDecimal(amount_to_pay.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal after = new BigDecimal(after_current_sheet.balance.toString()).setScale(2, RoundingMode.HALF_UP)

        // validation create payment
        extract_response == 201
        extract_id != null
        extract_asset_name == target_asset.name
        extract_contact_name == target_contact.name
        //extract_expense_name == target_expense.description
        extract_note == note
        extract_transaction_type_name == "payment"
        // validation current balance
        after == before.subtract(amount).setScale(2, RoundingMode.HALF_UP)
        // set for other test
        if (extract_id != null) {
            this.new_payment_transaction = extract_id
        }

    }// create payment record

    @Story("view the payment")
    @Description("""
        Get the payment record after create in previous test,
        It should be exist with response status 200
    """)
    @Severity(SeverityLevel.NORMAL)
    def "view the payment"() {

        given: "target id to perform GET, token, url"
        def transaction_id = this.new_payment_transaction
        def base_url = UrlManagement.paymentRecord
        def jwt_token = TokenManagement.instance.currentToken
        Allure.addAttachment("Request path param - Get Income Record", "application/json", transaction_id.toString(), ".json")

        when: "GET to retrieve income record"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(base_url, transaction_id, jwt_token)
        Allure.addAttachment("Response body - Get Payment Record", "application/json", response.path("data").toString(), ".json")

        then: "extract the value from response to perform validation"
        def extract_status = response.getStatusCode()
        def extract_id = response.path("data.id")

        extract_status == 200
        extract_id == transaction_id


    }// view the payment


    @Story("view all payment")
    @Description("""
        Get all payment record then check the on that create in previous test
        it should be exist with response status 200
""")
    @Severity(SeverityLevel.NORMAL)
    def "view all the payment"() {

        given: "url, token"
        Allure.step("1 of 4 : prepare url and jwt_token to perform GET")
        def base_url = UrlManagement.paymentRecord
        def jwt_token = TokenManagement.instance.currentToken


        when: "Send GET to retrieve all payment record"
        Allure.step("2 of 4 : Send GET to retrieve all payment record")
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(base_url, jwt_token)
        Allure.addAttachment("Response body - Get Payment Record", "application/json", response.path("data").toString(), ".json")


        then: "extract id to validate"
        Allure.step("3 of 4 : Extract all information from response")
        def extract_response = response.getStatusCode()
        def target_length = response.path("data.length")
        def target_validate = response.path("data.data.find { it.id == '${this.new_payment_transaction}'}")

        Allure.step("4 of 4 : Perform Validate, to check the response is correct or not")
        extract_response == 200
        target_length > 0
        target_validate != null


    }// view all payment


}// end class

//class ReqCreatePaymentDto {
//    String transaction_type_id
//    Double amount
//    String expense_id
//    String contact_id
//    String asset_id
//    String note
//
//}
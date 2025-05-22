package api_test.transaction.payment

import api_test.utility.HelperGetAsset
import api_test.utility.HelperGetContactId
import api_test.utility.HelperGetExpense
import api_test.utility.HelperGetTransactionType
import api_test.utility.TRANSACTION_TYPE
import dto.payment.ReqCreatePaymentDto
import dto.payment.ReqUpdatePaymentDto
import dto.payment.ResEntryPaymentDto
import groovy.transform.ToString
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
    @Shared
    String latest_asset_id
    @Shared
    String latest_asset_name
    @Shared
    String latest_amount

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




    @Story("update payment")
    @Feature("update payment")
    @Description("""
        After success create new payment record now try to update it,
        Also validate It before update and after update, is it return correct value
""")
    def "update payment"() {
        given: "payload to perform PUT, url, token"
        Allure.step("Prepare payload to perform PUT, url, token")
        def base_url = UrlManagement.paymentRecord
        def jwt_token = TokenManagement.instance.currentToken
        def amount_to_update = 72.36
        def target_expense = HelperGetExpense.get_expense_dto_for_update()
        def expense_id = target_expense.id
        def target_contact = HelperGetContactId.get_first_contact_detail_for_update()
        def contact_id = target_contact.id
        def target_asset = HelperGetAsset.get_asset_dto_for_update()
        def asset_id = target_asset.id
        def note_to_update = "test tis payment note${RandomUtility.generateRandom7DigitNumber()}"
        def updated_payload = new ReqUpdatePaymentDto(
            amount:                     amount_to_update,
            expense_id:                 expense_id,
            contact_id:                 contact_id,
            asset_id:                   asset_id,
            note:                       note_to_update
        )
        // set this to use in delete test
        this.latest_asset_name = target_asset.name
        this.latest_amount = amount_to_update
        this.latest_asset_id = target_asset.id
        Allure.addAttachment("Request body - Update Payment", "application/json", updated_payload.toString(), ".json")

        when: "GET and transform to ResEntryPaymentDto to check the value before update"
        Response payment_response = FetchApiResponseUtility.FetchGetByIdWithCredential(base_url, this.new_payment_transaction, jwt_token)
        def current_payment_info = new ResEntryPaymentDto(
                id:                 payment_response.path("data.id"),
                transaction_type_name:  payment_response.path("data.transaction_type_name"),
                amount:             payment_response.path("data.amount"),
                expense_name:       payment_response.path("data.expense_name"),
                contact_name:       payment_response.path("data.contact_name"),
                asset_name:         payment_response.path("data.asset_name"),
                note:               payment_response.path("data.note")
        )
        Allure.addAttachment("Response body - of Payment id ${this.new_payment_transaction} before perform PUT to update", "application/json", current_payment_info.toString(), ".json")


        then: "PUT with the payload and convert response into ResEntry to validate"
        Response payment_put_response = FetchApiResponseUtility.FetchUpdateWithCredential(base_url, updated_payload, jwt_token, this.new_payment_transaction)
        // convert response into dto
        def update_value_info = new ResEntryPaymentDto(
                id:                 payment_put_response.path("data.id"),
                transaction_type_name:  payment_put_response.path("data.transaction_type_name"),
                amount:             payment_put_response.path("data.amount"),
                expense_name:       payment_put_response.path("data.expense_name"),
                contact_name:       payment_put_response.path("data.contact_name"),
                asset_name:         payment_put_response.path("data.asset_name"),
                note:               payment_put_response.path("data.note")
        )

        expect: "validation area"
        Allure.step("start : validate get current_payment is correct response")
        payment_put_response.getStatusCode() == 200

        Allure.step("start : validate get current_payment mut not equal update_value")
        current_payment_info.id == update_value_info.id
        current_payment_info.transaction_type_name == update_value_info.transaction_type_name
        current_payment_info.contact_name != update_value_info.contact_name

        current_payment_info.asset_name != update_value_info.asset_name



        Allure.step("start: validate the value got from put is correct as in payload to perform update")
        BigDecimal payload_value = new BigDecimal(updated_payload.amount.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal response_value = new BigDecimal(update_value_info.amount.toString()).setScale(2, RoundingMode.HALF_UP)
        payload_value == response_value

    }// update payment



    @Story("delete payment")
    @Feature("delete payment")
    @Description("""
        after create and update Payment Record in previous function,
        now use that id to delete it
""")
    def "delete payment"() {
        given: "url, token"
        Allure.step("Prepare url, token, to perform DELETE")
        def base_url = UrlManagement.paymentRecord
        def jwt_token = TokenManagement.instance.currentToken
        def target_id = this.new_payment_transaction
        Allure.addAttachment("Request param - to perform Delete", "application/json", "${target_id}", ".json")

        when: "get current, and current after delete, and send DELETE method"
        Allure.step("check current balance before delete")
        def before_delete = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(this.latest_asset_id, this.latest_asset_name)
        Allure.addAttachment("before delete : current sheet of asset id ${this.latest_asset_id} name: ${this.latest_asset_name}", before_delete.toString(), ".json")

        Allure.step("send DELETE method")
        Response response = FetchApiResponseUtility.FetchDeleteWithCredential(base_url, jwt_token, target_id)
        Allure.addAttachment("Response Body - from perform Delete", "application/json", response.toString(), ".json")

        Allure.step("check current balance after delete")
        def after_delete = FetchCurrentSheetUtility.fetch_the_current_sheet_by_asset_id(this.latest_asset_id, this.latest_asset_name)
        Allure.addAttachment("after delete: current sheet of asset id ${this.latest_asset_id} name: ${this.latest_asset_name}", after_delete.toString(), ".json")

        then: "extract data from response to validate"
        Allure.step("extract data from response to validate")
        def delete_response_result = response.getStatusCode()
        // extract of current as the same type
        BigDecimal balance_before = new BigDecimal(before_delete.balance.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal balance_after = new BigDecimal(after_delete.balance.toString()).setScale(2, RoundingMode.HALF_UP)
        BigDecimal amount_to_decrease = new BigDecimal(this.latest_amount.toString()).setScale(2, RoundingMode.HALF_UP)


        expect: "all validation must be pass"
        Allure.step("validate the response status code expect : 200")
        delete_response_result == 200
        balance_before.add(amount_to_decrease) == balance_after
    }// delete

    @Story("get payment that deleted")
    @Feature("get income that deleted")
    @Description("""
        after delete payment in previous function, now check it is exist or not
""")
    def "get payment with the id that deleted"() {

        given: "url, token, target id"
        def base_url = UrlManagement.incomeRecord
        def jwt_token = TokenManagement.instance.currentToken
        def target_id = this.new_payment_transaction

        when: "call GET method"
        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(base_url, target_id, jwt_token)
        Allure.addAttachment("Response Body - from GET payment that deleted with id ${target_id}", "application/json", response.toString(), ".json")

        then: "validate the response"
        response.getStatusCode() == 404
    }



}// end class

//class ReqUpdatePaymentDto {
//    Double amount
//    String expense_id
//    String contact_id
//    String asset_id
//    String note
//}

//@ToString
//class ResEntryPaymentDto {
//    String id
//    String transaction_type_name
//    Double amount
//    String expense_name
//    String contact_name
//    String asset_name
//    String created_at
//    String updated_at
//}

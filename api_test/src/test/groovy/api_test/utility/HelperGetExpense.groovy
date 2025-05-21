package api_test.utility

import dto.expense.ResEntryExpenseDto
import groovy.transform.ToString
import helper.TokenManagement
import helper.UrlManagement
import helper.fetch.FetchApiResponseUtility
import io.restassured.response.Response

@SuppressWarnings("unused")
class HelperGetExpense {

    static ResEntryExpenseDto get_expense_dto_for_create() {
        def base_url = UrlManagement.baseExpense
        def token = TokenManagement.instance.currentToken
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(base_url, token)

        if (response.getStatusCode() != 200) {
            throw new IllegalStateException("Can't perform get expense response code isn't 200!!!")
        }

        def first_object = response.path("data.data[0]")
        response.prettyPrint()
        println("print first object")
        println(first_object)

        return new ResEntryExpenseDto(
                id:                 first_object.id,
                description:        first_object.description,
                expense_type_name:  first_object.expense_type_name,
                created_at:         first_object.created_at,
                updated_at:         first_object.updated_at
        )
    }// get_expense_dto_for_create

    static ResEntryExpenseDto get_expense_dto_for_update() {
        def base_url = UrlManagement.baseExpense
        def token = TokenManagement.instance.currentToken
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(base_url, token)

        if (response.getStatusCode() != 200) {
            throw new IllegalStateException("Can't perform get expense response code isn't 200!!!")
        }

        def first_object = response.path("data.data[2]")

        return new ResEntryExpenseDto(
                id:                 first_object.id,
                description:        first_object.description,
                expense_type_name:  first_object.expense_type_name,
                created_at:         first_object.created_at,
                updated_at:         first_object.updated_at
        )
    }// get_expense_dto_for_create

}




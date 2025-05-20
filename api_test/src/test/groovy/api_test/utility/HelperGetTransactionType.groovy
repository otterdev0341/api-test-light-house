package api_test.utility

import dto.expense.ResEntryTransactionTypeDto
import groovy.transform.ToString
import helper.TokenManagement
import helper.UrlManagement
import helper.fetch.FetchApiResponseUtility
import io.restassured.response.Response

enum TRANSACTION_TYPE {
    INCOME,
    PAYMENT,
    TRANSFER
}



class HelperGetTransactionType {

    static ResEntryTransactionTypeDto get_transaction_type(TRANSACTION_TYPE option) {
        def base_url = UrlManagement.getTransactionType()
        def token = TokenManagement.instance.currentToken
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(base_url, token)

        if (response.getStatusCode() != 200) {
            throw new IllegalStateException("Can't perform response check — code isn't 200!")
        }

        def nameToFind = option.name().toLowerCase()  // e.g. "income"

        def matchedMap = response.path("data.data.find { it.name == '${nameToFind}' }")

        if (!matchedMap) {
            throw new IllegalStateException("Transaction type '${nameToFind}' not found in response!")
        }

        // Map to DTO manually
        return new ResEntryTransactionTypeDto(
                id         : matchedMap.id,
                name       : matchedMap.name,
                created_at : matchedMap.created_at,
                updated_at : matchedMap.updated_at
        )
    }

}

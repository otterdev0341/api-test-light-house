package api_test.utility

import helper.TokenManagement
import helper.UrlManagement
import helper.fetch.FetchApiResponseUtility
import io.restassured.response.Response
import org.slf4j.Logger // For better logging
import org.slf4j.LoggerFactory // For better logging

class HelperGetExpenseTypeId {

    private static final Logger log = LoggerFactory.getLogger(HelperGetExpenseTypeId.class)

    // get first to create
    static String[] getFirstExpenseTypeDetail() {
        def url = UrlManagement.baseExpenseType
        def token = TokenManagement.instance.currentToken
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(url, token)

        def firstId = response.path("data.data[0].id")
        def expenseTypeName = response.path("data.data[0].name")



        return [firstId, expenseTypeName]
    }

    // get second to update
    static String[] getSecondExpenseTypeDetail() {
        def url = UrlManagement.baseExpenseType
        def token = TokenManagement.instance.currentToken
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(url, token)

        def secondId = response.path("data.data[1].id")
        def secondExpenseTypeName = response.path("data.data[1].name")

        println("Print from getSecondExpenseTypeDetail")
        response.prettyPrint()

        return [secondId, secondExpenseTypeName]
    }
}
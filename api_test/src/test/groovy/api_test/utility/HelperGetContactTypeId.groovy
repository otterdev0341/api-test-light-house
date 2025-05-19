package api_test.utility

import helper.TokenManagement
import helper.UrlManagement
import helper.fetch.FetchApiResponseUtility
import io.restassured.response.Response

class HelperGetContactTypeId {
    // get first
    static String[] getFirstContactTypeDetail() {
        def url = UrlManagement.baseContactType
        def token = TokenManagement.getInstance().currentToken
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(url, token)

        def first_id = response.path("data.data[0].id")
        def asset_name = response.path("data.data[0].name")
        return [first_id, asset_name]
    }
    // get second

    static String[] getSecondContactTypeDetail() {
        def url = UrlManagement.baseContactType
        def token = TokenManagement.getInstance().currentToken
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(url, token)

        def first_id = response.path("data.data[1].id")
        def asset_name = response.path("data.data[1].name")
        return [first_id, asset_name]
    }
}

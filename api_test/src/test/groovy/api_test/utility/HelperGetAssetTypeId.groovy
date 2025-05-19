package api_test.utility

import helper.TokenManagement
import helper.UrlManagement
import helper.fetch.FetchApiResponseUtility
import io.restassured.response.Response

class HelperGetAssetTypeId {

    static String getValidAssetTypeId() {
        def url = UrlManagement.baseAssetTypeUrl
        def token = TokenManagement.instance.currentToken

        Response get_all_asset_type = FetchApiResponseUtility.FetchGetAllWithCredential(url, token)

        get_all_asset_type.prettyPrint()
        def first_id = get_all_asset_type.path("data.data[0].id")
        return first_id
    }

    static String getValid2ndAssetTypeId() {
        def url = UrlManagement.baseAssetTypeUrl
        def token = TokenManagement.instance.currentToken

        Response get_all_asset_type = FetchApiResponseUtility.FetchGetAllWithCredential(url, token)

        get_all_asset_type.prettyPrint()
        def first_id = get_all_asset_type.path("data.data[1].id")
        return first_id
    }

    static String getAssetTypeNameById(String id){
        def url = UrlManagement.baseAssetTypeUrl
        def token = TokenManagement.instance.currentToken

        Response response = FetchApiResponseUtility.FetchGetByIdWithCredential(url, id ,token)
        response.prettyPrint()
        def asset_type_name = response.path("data.name")
        return asset_type_name


    }

}

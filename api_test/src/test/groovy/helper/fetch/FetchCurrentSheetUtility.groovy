package helper.fetch

import dto.current_sheet.ResEntryCurrentSheetDto
import helper.TokenManagement
import helper.UrlManagement
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification

class FetchCurrentSheetUtility {


    static Response fetch_all_current_sheet_by_user_id() {
        def base_url = UrlManagement.currentSheet
        def token = TokenManagement.instance.currentToken

        RequestSpecification request = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + token)
        Response response = request
                    .contentType(ContentType.JSON)
                    .get(base_url)
        if (response.statusCode != 200) {
            throw new IllegalStateException("response code is not 200, please review")
        }
        return response
    }

    static Response fetch_current_sheet_by_current_sheet_id(String current_sheet_id) {
        def prepared_url = UrlManagement.currentSheet + "/${current_sheet_id}"
        def token = TokenManagement.instance.currentToken

        RequestSpecification request = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + token)
        Response response = request
                    .contentType(ContentType.JSON)
                    .get(prepared_url)
        if (response.statusCode != 200) {
            throw new IllegalStateException("response code is not 200, please review")
        }
        return response

    }


    static Response fetch_all_current_sheet_by_asset_id(String asset_id) {
        def prepared_url = UrlManagement.currentSheet + "/${asset_id}/asset"
        def token = TokenManagement.instance.currentToken

        RequestSpecification request = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + token)
        Response response = request
                    .contentType(ContentType.JSON)
                    .get(prepared_url)
        if (response.statusCode != 200) {
            throw new IllegalStateException("response code is not 200, please review")
        }
        return response
    }

    static ResEntryCurrentSheetDto fetch_the_current_sheet_by_asset_id(String asset_id, String asset_name){
        def prepared_url = UrlManagement.currentSheet + "/${asset_id}/asset"
        def token = TokenManagement.instance.currentToken

        RequestSpecification request = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + token)
        Response response = request
                .contentType(ContentType.JSON)
                .get(prepared_url)
        if (response.statusCode != 200) {
            throw new IllegalStateException("response code is not 200, please review")
        }

        def matched = response.path("data.data.find { it.asset_name == '${asset_name}'} ")
        if (!matched) {
            throw new IllegalStateException("not found current sheet with asset id: ${asset_id} asset_name : ${asset_name}")
        }

        return new ResEntryCurrentSheetDto(
                id:                     matched.id,
                asset_name:             matched.asset_name,
                balance:                matched.balance,
                last_transaction_id:    matched.last_transaction_id,
                updated_at:             matched.updated_at
        )
    }
}

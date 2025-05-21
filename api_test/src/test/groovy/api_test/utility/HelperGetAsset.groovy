package api_test.utility

import dto.asset.ResEntryAssetDto
import helper.TokenManagement
import helper.UrlManagement
import helper.fetch.FetchApiResponseUtility
import io.restassured.response.Response

@SuppressWarnings("unused")
class HelperGetAsset {


    static ResEntryAssetDto get_asset_dto() {
        def base_url = UrlManagement.baseAssetUrl
        def token = TokenManagement.instance.currentToken
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(base_url, token)

        if (response.getStatusCode() != 200) {
            throw new IllegalStateException("Can't perform response check — code isn't 200!")
        }

        def target = response.path("data.data[0]")
        return new ResEntryAssetDto(
                id:             target.id,
                name:           target.name,
                asset_type:     target.asset_type,
                created_at:     target.created_at,
                updated_at:     target.updated_at
        )
    }

    static ResEntryAssetDto get_asset_dto_for_update() {
        def base_url = UrlManagement.baseAssetUrl
        def token = TokenManagement.instance.currentToken
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(base_url, token)

        if (response.getStatusCode() != 200) {
            throw new IllegalStateException("Can't perform response check — code isn't 200!")
        }

        def target = response.path("data.data[2]")
        return new ResEntryAssetDto(
                id:             target.id,
                name:           target.name,
                asset_type:     target.asset_type,
                created_at:     target.created_at,
                updated_at:     target.updated_at
        )
    }
}


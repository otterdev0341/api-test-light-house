package helper.fetch

import io.qameta.allure.Step
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification

@SuppressWarnings("unused")
class FetchApiResponseUtility {


    @Step("do api call to create new with post method")
    static Response FetchCreateWithCredential(
            String url,
            Object body,
            String jwt_token
    ){
        RequestSpecification request = RestAssured.given()
                .log().all() // Log request details for debugging
                .header("Authorization", "Bearer " + jwt_token)

        Response response = request
                .contentType(ContentType.JSON)
                .body(body)
                .post(url)

        return response
    }





    static Response FetchGetByIdWithCredential(
            String url,
            String target_id,
            String jwt_token
    ) {
        def prepared_url = url + "/${target_id}"
        RequestSpecification request = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + jwt_token)
        Response response = request
                .contentType(ContentType.JSON)
                .get(prepared_url)
        return response
    }

    static Response FetchGetAllWithCredential(
            String url,
            String jwt_token
    ){
        RequestSpecification request = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + jwt_token)
        Response response = request
                .contentType(ContentType.JSON)
                .get(url)

        return response
    }

    static Response FetchUpdateWithCredential(
            String url,
            Object body,
            String jwt_token,
            String target_id

    ){
        def prepared_url = url + "/${target_id}"
        RequestSpecification request = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + jwt_token)

        Response response = request
                .contentType(ContentType.JSON)
                .put(prepared_url)

        return response
    }

    static Response FetchDeleteWithCredential(
            String url,
            String jwt_token,
            String target_id

    ){
        RequestSpecification request = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + jwt_token)

        Response response = request
                .contentType(ContentType.JSON)
                .delete(url)

        return response
    }


    static Response FetUpdateUserWithCredential(
            String url,
            Object body,
            String jwt_token
    ){
        RequestSpecification request = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + jwt_token)
        Response response = request
                .contentType(ContentType.JSON)
                .body(body)
                .put(url)
        return response
    }
}
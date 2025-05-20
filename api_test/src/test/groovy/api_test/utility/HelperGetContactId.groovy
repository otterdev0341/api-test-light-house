package api_test.utility

import dto.contact.ResEntryContactDto
import groovy.transform.ToString
import helper.TokenManagement
import helper.UrlManagement
import helper.fetch.FetchApiResponseUtility
import io.restassured.response.Response

class HelperGetContactId {
    static ResEntryContactDto get_first_contact_detail() {
        def result = new ResEntryContactDto()
        def url = UrlManagement.baseContact
        def token = TokenManagement.instance.currentToken
        Response response = FetchApiResponseUtility.FetchGetAllWithCredential(url,token)

        if (response.getStatusCode() != 200) {
            throw new IllegalStateException("Response is not 200, please review")
        }

        def extract_item = response.path("data.data[0]")
        result.setId(extract_item.id)
        result.setName(extract_item.name)
        result.setBusiness_name(extract_item.business_name)
        result.setPhone(extract_item.phone)
        result.setDescription(extract_item.description)
        result.setContact_type(extract_item.contact_type)
        result.setCreated_at(extract_item.created_at)
        result.setUpdated_at(extract_item.updated_at)

        return result
    }
}



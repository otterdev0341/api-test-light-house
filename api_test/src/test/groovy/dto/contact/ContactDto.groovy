package dto.contact

import groovy.transform.ToString

@ToString
class ReqCreateContactDto {
    String name
    String business_name
    String phone
    String description
    String contact_type_id
}

@ToString
class ReqUpdateContactDto {
    String name
    String business_name
    String phone
    String description
    String contact_type_id
}


@ToString
class ResEntryContactDto {
    String id
    String name
    String business_name
    String phone
    String description
    String contact_type
    String created_at
    String updated_at
}



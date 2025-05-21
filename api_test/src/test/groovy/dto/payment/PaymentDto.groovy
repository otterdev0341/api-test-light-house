package dto.payment

import groovy.transform.ToString


@ToString
class ReqCreatePaymentDto {
    String transaction_type_id
    Double amount
    String expense_id
    String contact_id
    String asset_id
    String note

}



@ToString
class ReqUpdatePaymentDto {
    String transaction_type_id
    Double amount
    String expense_id
    String contact_id
    String asset_id
    String note


}


@ToString
class ResEntryPaymentDto {
    String id
    String transaction_type_name
    Double amount
    String expense_name
    String contact_name
    String asset_name
    String created_at
    String updated_at
}

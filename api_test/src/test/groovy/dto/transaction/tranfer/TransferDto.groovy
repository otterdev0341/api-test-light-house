package dto.transaction.tranfer

import groovy.transform.ToString


@ToString
class ReqCreateTransferDto {
    String transaction_type_id
    Double amount
    String asset_id
    String destination_asset_id
    String contact_id
    String note
}


@ToString
class ReqUpdateTransferDto {
    Double amount
    String asset_id
    String destination_asset_id
    String contact_id
    String note
}


@ToString
class ResEntryTransferDto {
    String id
    String transaction_type_name
    String amount
    String asset_name
    String destination_asset_name
    String contact_name
    String note
    String created_at
    String updated_at
}

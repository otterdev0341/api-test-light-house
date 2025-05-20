package dto.transaction.income

import groovy.transform.ToString


@ToString
class ReqCreateIncomeDto {
    String transaction_type_id // income, transfer, expense,
    Double amount
    String asset_id
    String contact_id
    String note
}

@ToString
class ReqUpdateIncomeDto {
    Double amount
    String asset_id
    String contact_id
    String note
}

@ToString
class ResEntryIncomeDto {
    String id
    String transaction_type_name
    Double amount
    String asset_name
    String contact_name
    String note
    String updated_at
}


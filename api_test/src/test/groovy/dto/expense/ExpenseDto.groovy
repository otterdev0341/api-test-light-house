package dto.expense

import groovy.transform.ToString

@ToString
class ReqCreateExpenseDto {
    String description
    String expense_type_id
}


@ToString
class ReqUpdateExpenseDto {
    String description
    String expense_type_id
}

@ToString
class ResEntryTransactionTypeDto {
    String id
    String name
    String created_at
    String updated_at
}

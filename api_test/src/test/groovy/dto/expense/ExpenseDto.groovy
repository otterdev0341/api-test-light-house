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
class ResEntryExpenseDto {
    String id
    String description
    String expense_type_name
    String created_at
    String updated_at
}



@ToString
class ResEntryTransactionTypeDto {
    String id
    String name
    String created_at
    String updated_at
}

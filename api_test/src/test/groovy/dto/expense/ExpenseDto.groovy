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

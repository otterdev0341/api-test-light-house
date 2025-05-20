package dto.current_sheet

import groovy.transform.ToString

@ToString
class ResEntryCurrentSheetDto {
    String asset_name
    Double balance
    String id
    String last_transaction_id
    String updated_at
}


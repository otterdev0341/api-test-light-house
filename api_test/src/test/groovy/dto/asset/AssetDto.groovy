package dto.asset

import groovy.transform.ToString


@ToString
class RequestCreateAssetDto {
    String name
    String asset_type_id
}


@ToString
class RequestUpdateAssetDto {
    String name
    String asset_type_id
}

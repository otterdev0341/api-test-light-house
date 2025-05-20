package dto.asset

import groovy.transform.ToString


@ToString
class ReqCreateAssetDto {
    String name
    String asset_type_id
}


@ToString
class ReqUpdateAssetDto {
    String name
    String asset_type_id
}


@ToString
class ResEntryAssetDto {
    String id
    String name
    String asset_type
    String created_at
    String updated_at
}



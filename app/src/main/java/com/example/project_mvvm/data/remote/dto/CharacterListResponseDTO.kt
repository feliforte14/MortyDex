package com.example.project_mvvm.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PageInfoDto(
    @Json(name = "count") val count: Int,
    @Json(name = "pages") val pages: Int,
    @Json(name = "next") val next: String?,
    @Json(name = "prev") val prev: String?
)

@JsonClass(generateAdapter = true)
data class CharacterListResponseDto(
    @Json(name = "info") val info: PageInfoDto,
    @Json(name = "results") val results: List<CharacterDto>
)

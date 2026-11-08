package com.example.project_mvvm.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationDto(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)

@JsonClass(generateAdapter = true)
data class CharacterDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "status") val status: String,
    @Json(name = "species") val species: String,
    @Json(name = "gender") val gender: String,
    @Json(name = "origin") val origin: LocationDto,
    @Json(name = "location") val location: LocationDto,
    @Json(name = "image") val image: String,
    @Json(name = "episode") val episode: List<String>
)
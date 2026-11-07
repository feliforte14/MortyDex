package com.example.project_mvvm.domain.model

data class CharacterDetail(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val gender: String,
    val origin: String,
    val location: String,
    val imageUrl: String,
    val episodeCount: Int
)
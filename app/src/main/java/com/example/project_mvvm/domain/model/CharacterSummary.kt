package com.example.project_mvvm.domain.model

data class CharacterSummary(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val imageUrl: String

)

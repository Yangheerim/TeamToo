package com.example.teamtotest.dto

data class FileDTO(
    val fileName : String ="",
    val date : String = "",
    val uid : String = "",
    val userName : String = "",
    var projectdata : ProjectDTO? = null
)
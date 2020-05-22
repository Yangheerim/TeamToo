package com.example.teamtotest.dto

data class TodoDTO(
    var name: String? = "",
    var note: String? = "",
    var deadLine: Long = 0,
    var performers : ArrayList<String> = arrayListOf(),
    var alarm: Int = 0,
    var projectdata : ProjectDTO? =null
)
package com.example.teamtotest.dto

data class MessageDTO(
    val message : String ="",
    val who : String="",
    val userUID : String ="",
    val read : ArrayList<String>? = ArrayList<String>(),
    val todoData : TodoDTO ?=null,
    val scheduleData : ScheduleDTO ?=null
)
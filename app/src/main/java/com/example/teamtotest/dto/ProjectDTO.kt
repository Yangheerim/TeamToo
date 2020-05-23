package com.example.teamtotest.dto

import java.util.*

data class ProjectDTO (
    val projectName : String = "",
    var startDate : Date? =null,
    var endDate : Date? =null
    )
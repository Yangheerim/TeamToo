package com.example.teamtotest.dto

import java.util.*

data class ProjectDTO (
    val projectName : String = "",
    var progressData : ProgressDTO ?= null,
    var pid : String? = null
)
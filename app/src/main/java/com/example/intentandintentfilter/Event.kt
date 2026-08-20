package com.example.intentandintentfilter

import java.io.Serializable

data class Event(
    val id: Int,
    val name: String,
    val description: String,
    val timeInMillis: Long
) : Serializable

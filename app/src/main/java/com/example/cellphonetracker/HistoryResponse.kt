package com.example.cellphonetracker

data class HistoryResponse(
    val device: String,
    val id: String,
    val lat: String,
    val lng: String,
    val time: String
)
package com.gladysassistant.android.auto.data.model

data class Dashboard(
    val id: String,
    val name: String,
    val boxes: List<List<Box>?>,
)

data class Box(
    val name: String,
    val type: String,
    val device_features: List<String>,
)


package com.gladysassistant.android.auto.data.model

data class DeviceSetValueResponse(
    val type: String,
    val device: String,
    val device_feature: String,
    val feature_category: String,
    val feature_type: String,
    val value: String,
    val status: String,
)
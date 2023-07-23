package com.gladysassistant.android.auto.data

object Constants {
    const val API_USER = "API_USER"
    val API_PASSWORD = "API_PASSWORD"
    const val ACCESS_TOKEN = "access_token"
    const val REFRESH_TOKEN = "refresh_token"
    const val apiUrl = "https://home.rpochet.be/api/v1"
    //val apiUrl = "https://192.168.1.4/api/v1"
    const val loginUrl = "$apiUrl/login"
    const val tokenUrl = "$apiUrl/token"
    const val deviceUrl = "$apiUrl/device"
    const val deviceFeatureSetValueUrl = "$apiUrl/device_feature/%s/value"
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_AUTHORIZATION_BEARER = "Bearer"
    const val CONTENT_TYPE = "Content-Type"
    const val APPLICATION_JSON = "application/json"
    val ROOM_AUTO = "Auto"

    const val CATEGORY_SWITCH = "switch"
    const val TYPE_BINARY = "binary"
    const val TYPE_DIMMER = "dimmer"
    val ALLOWED_CATEGORY = mutableListOf(CATEGORY_SWITCH)
    val ALLOWED_TYPE = mutableListOf(TYPE_BINARY)
}
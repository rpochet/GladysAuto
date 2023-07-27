package com.gladysassistant.android.auto.data

object Constants {
    const val PREFERENCE_NAME = "Gladys"
    const val USER_ID = "USER_ID"
    const val USER_PASSWORD = "USER_PASSWORD"
    // const val apiUrl = "https://home.rpochet.be/api/v1"
    // const val apiUrl = "https://192.168.1.4/api/v1"
    const val apiUrl = "https://91.177.205.233/api/v1"
    const val loginUrl = "$apiUrl/login"
    const val tokenUrl = "$apiUrl/access_token"
    const val autoDevicesUrl = "$apiUrl/dashboard/auto/device_feature"
    const val deviceFeatureSetValueUrl = "$apiUrl/device_feature/%s/value"
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_AUTHORIZATION_BEARER = "Bearer"
    const val CONTENT_TYPE = "Content-Type"
    const val APPLICATION_JSON = "application/json"

    const val PARAM_EMAIL = "email"
    const val PARAM_PASSWORD = "password"
    const val PARAM_CLIENT_ID = "client_id"
    const val PARAM_CLIENT_SECRET = "client_secret"
    const val PARAM_ACCESS_TOKEN = "access_token"
    const val PARAM_REFRESH_TOKEN = "refresh_token"
    const val PARAM_GRANT_TYPE = "grant_type"

    const val CATEGORY_SWITCH = "switch"
    const val TYPE_BINARY = "binary"
    const val TYPE_DIMMER = "dimmer"
    /*val ALLOWED_CATEGORY = mutableListOf(CATEGORY_SWITCH)
    val ALLOWED_TYPE = mutableListOf(TYPE_BINARY)*/
}
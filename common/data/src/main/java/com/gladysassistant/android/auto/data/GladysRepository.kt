/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gladysassistant.android.auto.data

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.Volley
import com.gladysassistant.android.auto.data.model.Device
import com.gladysassistant.android.auto.data.model.DeviceFeature
import com.gladysassistant.android.auto.data.model.DeviceSetValueResponse
import org.apache.http.conn.ssl.AllowAllHostnameVerifier
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.reflect.typeOf

class GladysRepository(
    private val ctx: Context
) {
    private var requestQueue: RequestQueue = Volley.newRequestQueue(ctx, object : HurlStack() {
        override fun createConnection(url: URL): HttpURLConnection {
            val connection = super.createConnection(url)
            if (connection is HttpsURLConnection) {
                connection.hostnameVerifier = AllowAllHostnameVerifier()
            }
            return connection
        }
    })

    fun login(callback: (String) -> Unit) {
        val loginRequest = GsonRequest<Map<String, String>>(
            Request.Method.POST,
            Constants.loginUrl,
            mutableMapOf(
                "email" to "pochet.romuald@gmail.com",
                "password" to "8Vyr7acpromuald"
            ),
            typeOf<Map<String, String>>(),
            mutableMapOf(Constants.CONTENT_TYPE to Constants.APPLICATION_JSON),
            {
                val accessToken = it.getValue(Constants.ACCESS_TOKEN)
                StoreUtils.set(Constants.ACCESS_TOKEN, accessToken)
                StoreUtils.set(Constants.REFRESH_TOKEN, it.getValue(Constants.REFRESH_TOKEN))
                callback(accessToken)
            }
        ) { }
        this.requestQueue.add(loginRequest)
    }
    fun refreshAccessToken(callback: (String) -> Unit) {
        val refreshTokenRequest = GsonRequest<Map<String, String>>(
            Request.Method.POST,
            Constants.tokenUrl,
            mutableMapOf(
                "client_id" to "pochet.romuald@gmail.com",
                "client_secret" to "8Vyr7acpromuald",
                "refresh_token" to "${StoreUtils.get("$Constants.REFRESH_TOKEN")}",
                "grant_type" to "refresh_token"
            ),
            typeOf<Map<String, String>>(),
            mutableMapOf(Constants.CONTENT_TYPE to Constants.APPLICATION_JSON),
            {
                val accessToken = it.getValue(Constants.ACCESS_TOKEN)
                StoreUtils.set(Constants.ACCESS_TOKEN, accessToken)
                // retryRequets.headers.set(Constants.HEADER_AUTHORIZATION, Constants.HEADER_AUTHORIZATION_BEARER + accessToken)
                // this.requestQueue.add(retryRequets)
                callback(accessToken)
            }
        ) { }
        this.requestQueue.add(refreshTokenRequest)
    }

    fun getDeviceFeatures(callback: (List<DeviceFeature>) -> Unit) {
        val request = GsonRequest<List<Device>>(
            Request.Method.GET,
            Constants.deviceUrl,
            null,
            typeOf<List<Device>>(),
            mutableMapOf(Constants.HEADER_AUTHORIZATION to "${Constants.HEADER_AUTHORIZATION_BEARER} ${StoreUtils.get(Constants.ACCESS_TOKEN)}"),
            {
                var deviceFeatures = it
                    //.filter { device -> device.room?.name.equals(Constants.ROOM_AUTO) }
                    .flatMap {
                        it.features
                    }
                    .filter { it.category in Constants.ALLOWED_CATEGORY && it.type in Constants.ALLOWED_TYPE }
                    .toList()
                callback(deviceFeatures)
            }
        ) {
            if( it is AuthFailureError) {
                // refreshAccessToken(this::getDeviceFeatures, arrayOf(callback))
                login {
                    getDeviceFeatures(callback)
                }
            }
        }
        this.requestQueue.add(request)
    }

    fun getDeviceFeature(deviceFeatureId: String): DeviceFeature? {
        return emptyList<DeviceFeature>().find { it.external_id == deviceFeatureId }
    }

    fun updateDeviceFeature(deviceFeature: DeviceFeature, callback: (DeviceSetValueResponse) -> Unit) {
        var newValue = 0;
        if (deviceFeature.last_value!! > 0) {
            newValue = 0
        } else {
            newValue = 1
        }
        val request = GsonRequest<DeviceSetValueResponse>(
            Request.Method.POST,
            Constants.deviceFeatureSetValueUrl.format(deviceFeature.selector),
            mutableMapOf(
                "value" to newValue
            ),
            typeOf<DeviceSetValueResponse>(),
            mutableMapOf(
                Constants.HEADER_AUTHORIZATION to "${Constants.HEADER_AUTHORIZATION_BEARER} ${StoreUtils.get(Constants.ACCESS_TOKEN)}",
                Constants.CONTENT_TYPE to Constants.APPLICATION_JSON
            ),
            {
                callback(it)
            },
            {

            }
        );
        this.requestQueue.add(request)
    }

}
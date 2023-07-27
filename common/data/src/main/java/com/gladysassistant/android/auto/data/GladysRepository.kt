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
import android.content.Context.MODE_PRIVATE
import androidx.car.app.CarContext
import androidx.datastore.preferences.preferencesDataStore
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.Volley
import com.gladysassistant.android.auto.data.model.Dashboard
import com.gladysassistant.android.auto.data.model.Device
import com.gladysassistant.android.auto.data.model.DeviceFeature
import com.gladysassistant.android.auto.data.model.DeviceSetValueResponse
import com.gladysassistant.android.utils.StoreUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.http.conn.ssl.AllowAllHostnameVerifier
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.typeOf

class GladysRepository(
    private val ctx: Context
) {
    // private val Context.dataStore by preferencesDataStore(name = Constants.PREFERENCE_NAME)

    private var requestQueue: RequestQueue = Volley.newRequestQueue(ctx, object : HurlStack() {
        override fun createConnection(url: URL): HttpURLConnection {
            val connection = super.createConnection(url)
            if (connection is HttpsURLConnection) {
                connection.hostnameVerifier = AllowAllHostnameVerifier()
            }
            return connection
        }
    })

    suspend fun login() = suspendCoroutine { cont ->
        val loginRequest = GsonRequest<Map<String, String>>(
            Request.Method.POST,
            Constants.loginUrl,
            /*GlobalScope.launch {
                StoreUtils.getInstance(ctx.dataStore).userPreferencesFlow.apply {
                    this.first().apply {
                        mutableMapOf(
                            Constants.PARAM_EMAIL to this.userId,
                            Constants.PARAM_PASSWORD to this.userPassword,
                        )
                    }
                }
            },*/
            mutableMapOf(
                Constants.PARAM_EMAIL to "pochet.romuald@gmail.com",
                Constants.PARAM_PASSWORD to "8Vyr7acpromuald"
            ),
            typeOf<Map<String, String>>(),
            mutableMapOf(
                Constants.CONTENT_TYPE to Constants.APPLICATION_JSON
            ),
            {
                val accessToken = it.getValue(Constants.PARAM_ACCESS_TOKEN)
                ctx.getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE).edit().apply {
                    putString(Constants.PARAM_ACCESS_TOKEN, accessToken)
                    putString(Constants.PARAM_REFRESH_TOKEN, it.getValue(Constants.PARAM_REFRESH_TOKEN))
                    commit()
                }
                cont.resume(accessToken)
            }
        ) {
            System.err.println(it.message)
        }
        this.requestQueue.add(loginRequest)
    }
    suspend fun refreshAccessToken() = suspendCoroutine { cont ->
        val refreshTokenRequest = GsonRequest<Map<String, String>>(
            Request.Method.POST,
            Constants.tokenUrl,
            mutableMapOf(
                Constants.PARAM_CLIENT_ID to ctx.getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE).getString(Constants.USER_ID, "pochet.romuald@gmail.com"),
                Constants.PARAM_CLIENT_SECRET to ctx.getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE).getString(Constants.USER_PASSWORD, "8Vyr7acpromuald"),
                Constants.PARAM_REFRESH_TOKEN to ctx.getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE).getString(Constants.PARAM_REFRESH_TOKEN, ""),
                Constants.PARAM_GRANT_TYPE to Constants.PARAM_REFRESH_TOKEN
            ),
            typeOf<Map<String, String>>(),
            mutableMapOf(Constants.CONTENT_TYPE to Constants.APPLICATION_JSON),
            {
                val accessToken = it.getValue(Constants.PARAM_ACCESS_TOKEN)
                ctx.getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE).edit().apply {
                    putString(Constants.PARAM_ACCESS_TOKEN, accessToken)
                    commit()
                }
                cont.resume(accessToken)
            }
        ) {
            if (it is AuthFailureError) {
                System.err.println("Refresh token is invalid, login...")
                GlobalScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Default) {
                        login()
                    }
                }
            } else {
                cont.resumeWithException(it)
            }
        }
        this.requestQueue.add(refreshTokenRequest)
    }

    suspend fun getDeviceFeatures(): List<DeviceFeature> = suspendCoroutine { cont ->
        val request = GsonRequest<List<DeviceFeature>>(
            Request.Method.GET,
            Constants.autoDevicesUrl,
            null,
            typeOf<List<DeviceFeature>>(),
            /*GlobalScope.launch {
                StoreUtils.getInstance(ctx.dataStore).tokenPreferencesFlow.apply {
                    this.first().apply {
                        mutableMapOf(
                            Constants.HEADER_AUTHORIZATION to "${Constants.HEADER_AUTHORIZATION_BEARER} ${this.accessToken}"
                        )
                    }
                }
            },*/
            mutableMapOf(
                Constants.HEADER_AUTHORIZATION to "${Constants.HEADER_AUTHORIZATION_BEARER} ${ctx.getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE).getString(Constants.PARAM_ACCESS_TOKEN, "")}"
            ),
            cont::resume
        ) {
            if (it is AuthFailureError) {
                System.err.println("Refresh access token...")
                GlobalScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Default) {
                        refreshAccessToken()
                        // login()
                        cont.resume(getDeviceFeatures())
                    }
                }
            } else {
                cont.resumeWithException(it)
            }
        }
        this.requestQueue.add(request)
    }

    suspend fun updateDeviceFeature(deviceFeature: DeviceFeature): DeviceSetValueResponse = suspendCoroutine<DeviceSetValueResponse> { cont ->
        var newValue = 0;
        if (deviceFeature.last_value > 0) {
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
                Constants.HEADER_AUTHORIZATION to "${Constants.HEADER_AUTHORIZATION_BEARER} ${ctx.getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE).getString(Constants.PARAM_ACCESS_TOKEN, "")}",
                Constants.CONTENT_TYPE to Constants.APPLICATION_JSON
            ),
            {
                cont.resume(it)
            },
            {
                if (it is AuthFailureError) {
                    // this::getDeviceFeatures, arrayOf(callback))
                    GlobalScope.launch {
                        //login()
                        refreshAccessToken()
                        cont.resume(updateDeviceFeature(deviceFeature))
                    }
                }
                cont.resumeWithException(it)
            }
        );
        this.requestQueue.add(request)
    }

}
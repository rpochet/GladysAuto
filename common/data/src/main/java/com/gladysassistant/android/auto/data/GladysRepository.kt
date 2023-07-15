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

import com.gladysassistant.android.auto.data.model.DeviceFeature

private const val url = "https://home.rpochet.be/api/v1/device"
/*private val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}*/

val DEVICE_FEATURES = listOf(
    DeviceFeature(
        "zwave-js-ui:node_id:12:comclass:38:endpoint:0:property:targetValue",
        "RGBWE27ZW - 12 - Current value",
        "switch",
        "dimmer"
    ),
    DeviceFeature(
        "zwave-js-ui:node_id:148:comclass:37:endpoint:1:property:targetValue",
        "FGS223 - 148 [1] - Target value [1]",
        "switch",
        "binary"
    ),
    DeviceFeature(
        "zwave-js-ui:node_id:49:comclass:37:endpoint:0:property:targetValue",
        "HKZW-SO05 - 49 - Target value",
        "switch",
        "binary"
    ),
)

class GladysRepository {
    fun getDeviceFeatures(): List<DeviceFeature> {
        return DEVICE_FEATURES
    }

    fun getDeviceFeature(deviceFeatureId: String): DeviceFeature? {
        return DEVICE_FEATURES.find { it.externalId == deviceFeatureId }
    }
}
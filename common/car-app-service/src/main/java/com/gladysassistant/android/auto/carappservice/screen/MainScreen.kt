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

package com.gladysassistant.android.auto.carappservice.screen

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.core.graphics.drawable.IconCompat
import com.gladysassistant.android.auto.carappservice.R
import com.gladysassistant.android.auto.data.GladysRepository
import com.gladysassistant.android.auto.data.model.DeviceFeature

class MainScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val gladysRepository = GladysRepository()
        val itemListBuilder = ItemList.Builder()
            .setNoItemsMessage("No devices to show")

        gladysRepository.getDeviceFeatures()
            .forEach {
                itemListBuilder.addItem(
                    GridItem.Builder()
                        .setTitle(it.name)
                        // Each item in the list *must* have a DistanceSpan applied to either the title
                        // or one of the its lines of text (to help drivers make decisions)
                        .setImage(CarIcon.Builder(
                            IconCompat
                            .createWithResource(carContext, getDrawable(it)))
                            .setTint(CarColor.BLUE)
                            .build()
                        )
                        .setLoading(false)
                        .setOnClickListener {
                            CarToast.makeText(carContext, it.name, CarToast.LENGTH_LONG)
                        }
                        .build()
                )
            }

        return GridTemplate.Builder()
            .setTitle("Devices")
            .setSingleList(itemListBuilder.build())
            .build()
    }
}

private fun getDrawable(deviceFeature: DeviceFeature): Int {
    if (deviceFeature.category == "switch" && deviceFeature.type == "dimmer") {
        return R.drawable.lightbulb_on_outline;
    } else {
        return R.drawable.crosshairs_question;
    }
}
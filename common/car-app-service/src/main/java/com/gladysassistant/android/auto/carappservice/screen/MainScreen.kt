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
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.InputCallback
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.car.app.model.signin.InputSignInMethod
import androidx.car.app.model.signin.SignInTemplate
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gladysassistant.android.auto.data.Constants
import com.gladysassistant.android.auto.carappservice.IconUtils
import com.gladysassistant.android.auto.carappservice.R
import com.gladysassistant.android.auto.data.GladysRepository
import com.gladysassistant.android.auto.data.StoreUtils
import com.gladysassistant.android.auto.data.model.DeviceFeature
import kotlinx.coroutines.launch

class MainScreen(carContext: CarContext) : Screen(carContext) {

    private val gladysRepository = GladysRepository(carContext)

    private val deviceFeatures = mutableListOf<DeviceFeature>()

    init {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gladysRepository.getDeviceFeatures {
                    deviceFeatures.addAll(it)
                    invalidate()
                }
            }
        }
    }

    override fun onGetTemplate(): Template {
        val itemListBuilder = ItemList.Builder()
            .setNoItemsMessage(carContext.getString(R.string.no_devices))

        if (StoreUtils.get(Constants.REFRESH_TOKEN)?.isEmpty() == true) {
            return SignInTemplate.Builder(InputSignInMethod.Builder(object : InputCallback {
                override fun onInputSubmitted(text: String) {
                    StoreUtils.set(Constants.API_USER, text)
                }
            }).build()).apply {
                setTitle(carContext.getString(R.string.configuration))
                setInstructions(carContext.getString(R.string.configuration_instructions))
            }.build()
        } else {
            deviceFeatures.forEach { deviceFeature ->
                itemListBuilder.addItem(
                    GridItem.Builder()
                        .setTitle(deviceFeature.name)
                        .setImage(
                            IconUtils.getDeviceFeature(carContext, deviceFeature)
                        )
                        .setLoading(false)
                        .setOnClickListener {
                            CarToast.makeText(carContext, deviceFeature.name, CarToast.LENGTH_LONG)
                            gladysRepository.updateDeviceFeature(deviceFeature) {
                                invalidate()
                            }
                        }
                        .build()
                )
            }
            return GridTemplate.Builder().apply {
                setTitle(carContext.getString(R.string.devices))
                setActionStrip(
                    ActionStrip.Builder().addAction(
                        Action.Builder()
                            .setIcon(IconUtils.getApp(carContext))
                            .setOnClickListener {
                                screenManager.push(SettingsScreen(carContext));
                            }.build()
                    ).build()
                )
                if (deviceFeatures.isEmpty()) {
                    setLoading(true)
                } else {
                    setLoading(false)
                    setSingleList(itemListBuilder.build())
                }
            }.build()
        }
    }
}
package com.gladysassistant.android.auto.data.model

import android.content.Intent
import androidx.core.net.toUri

data class Device(
    val externalId: String,
    val name: String,
    val features: List<DeviceFeature>,
    val room: Room?,
)

data class Room(
    val name: String,
)

fun Device.toIntent(action: String): Intent {
    return Intent(action).apply {
        data = "gladys:$externalId".toUri()
    }
}
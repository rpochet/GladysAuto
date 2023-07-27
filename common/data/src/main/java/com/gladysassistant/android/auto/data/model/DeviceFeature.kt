package com.gladysassistant.android.auto.data.model

import android.content.Intent
import androidx.core.net.toUri

data class DeviceFeature(
    val external_id: String,
    val selector: String,
    val name: String,
    val category: String,
    val type: String,
    val last_value: Float
)

fun DeviceFeature.toIntent(action: String): Intent {
    return Intent(action).apply {
        data = "gladys:$external_id".toUri()
    }
}
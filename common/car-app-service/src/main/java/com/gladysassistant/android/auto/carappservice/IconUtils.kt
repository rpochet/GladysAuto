package com.gladysassistant.android.auto.carappservice

import android.content.Context
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import com.gladysassistant.android.auto.data.Constants.CATEGORY_SWITCH
import com.gladysassistant.android.auto.data.Constants.TYPE_BINARY
import com.gladysassistant.android.auto.data.Constants.TYPE_DIMMER
import com.gladysassistant.android.auto.data.model.DeviceFeature

object IconUtils {

    fun getDeviceFeature(ctx: Context, deviceFeature: DeviceFeature): CarIcon {
        val resId: Int
        if (deviceFeature.category == CATEGORY_SWITCH &&
            (deviceFeature.type == TYPE_BINARY || deviceFeature.type == TYPE_DIMMER)) {
            if (deviceFeature.last_value != null && deviceFeature.last_value!! > 0)
                resId = R.drawable.lightbulb_on_outline
            else
                resId = R.drawable.lightbulb_outline
        } else {
            resId = R.drawable.crosshairs_question;
        }
        return CarIcon.Builder(
            IconCompat
                .createWithResource(ctx, resId)
            )
            .setTint(CarColor.BLUE)
            .build()
    }

    fun getApp(ctx: Context): CarIcon {
        return CarIcon.Builder(
            IconCompat
                .createWithResource(ctx, R.drawable.cog)
            )
            .setTint(CarColor.BLUE)
            .build()
    }

}
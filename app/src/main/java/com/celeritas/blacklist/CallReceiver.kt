package com.celeritas.blacklist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.telephony.TelephonyManager

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

                val numberNotification = Intent(NumberNotificationData.EVENT_NAME)
                numberNotification.putExtra(NumberNotificationData.FIELD, number)

                val notificationContext = context ?: return

                LocalBroadcastManager.getInstance(notificationContext).sendBroadcast(numberNotification)
            }
        }
    }

    companion object {
        fun endCall(context: Context) {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager ?: return

            val getITelephony = telephonyManager.javaClass.getDeclaredMethod("getITelephony")

            getITelephony.isAccessible = true

            val telephonyService = getITelephony.invoke(telephonyManager)

            val telephonyServiceClass = Class.forName(telephonyService.javaClass.name)

            val endCallMethod = telephonyServiceClass.getDeclaredMethod("endCall")

            endCallMethod.isAccessible = true

            endCallMethod.invoke(telephonyService)
        }
    }
}

object NumberNotificationData {
    const val EVENT_NAME = "number-received"
    const val FIELD = "number"
}

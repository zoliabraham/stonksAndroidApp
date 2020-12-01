package com.zoliabraham.stonks.backgroundTask

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


//this is fine for demonstration purposes, but a lot of phones kill background services,
//so, this should be implemented using a backend and fcm, or similar
class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val updateIntent = Intent(context, UpdateService::class.java)
        if (context != null) {
            UpdateService.enqueueWork(context, updateIntent)
        }

    }
}
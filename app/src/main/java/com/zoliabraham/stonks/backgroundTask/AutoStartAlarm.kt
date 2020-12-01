package com.zoliabraham.stonks.backgroundTask

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutoStartAlarm : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == Intent.ACTION_BOOT_COMPLETED)
            scheduleAlarm(context)
    }

    private fun scheduleAlarm(context: Context?) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val alarmIntent = Intent(context, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, 0)

        alarmManager?.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, 60000, pendingIntent)
    }

}
@file:Suppress("unused", "unused")

package com.zoliabraham.stonks.backgroundTask

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import com.zoliabraham.stonks.MainActivity
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.data.notificationList.NotificationListDatabase
import com.zoliabraham.stonks.data.notificationList.NotificationListItem
import com.zoliabraham.stonks.data.reminder.ReminderItem

//https://stackoverflow.com/a/62140756/12559737
class UpdateService : JobIntentService(){
    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, UpdateService::class.java, 1, intent)
        }
    }

    private lateinit var reminderList: List<ReminderItem>

    // This method is called when service starts instead of onHandleIntent
    override fun onHandleWork(intent: Intent) {
        /*val database = Room.databaseBuilder(application, ReminderListDatabase::class.java,"reminder-list").build()
        reminderList = database.reminderListDao().getAll()
        val stockList = convertReminderDataToStockData(reminderList)
        StockDataListDownloader(stockList, ::onStockDataDownloaded, true) //run blocking, bc it already runs on a background thread, if not, service could be terminated
        database.close()*/

        NotificationUpdater(application, ::sendNotification)
    }

    // remove override and make onHandleIntent private.
    private fun onHandleIntent(intent: Intent?) {}


    /*private fun convertReminderDataToStockData(list: List<ReminderItem>): ArrayList<StockData> {
        val uniqueSymbols = ArrayList<String>()
        val stockDataList = ArrayList<StockData>()

        list.forEach {
            if(!uniqueSymbols.contains(it.symbol)){
                uniqueSymbols.add(it.symbol)
            }
        }

        uniqueSymbols.forEach {
            stockDataList.add(StockData(symbol = it))
        }

        return stockDataList

    }*/

    /*private fun onStockDataDownloaded(data: ArrayList<StockData>){
        val activeNotifications = getActiveNotifications(data)

        if(activeNotifications.isNotEmpty()) {
            sendNotification(activeNotifications)
            saveToDatabase(activeNotifications)
        }
    }*/

    /*private fun getActiveNotifications(data: ArrayList<StockData>): ArrayList<NotificationListItem>{
        val activeNotifications = ArrayList<NotificationListItem>()

        reminderList.forEach { reminderItem ->
            data.forEach {stockData ->
                val notificationListItem = getMatchingNotification(reminderItem, stockData)
                if(notificationListItem!=null){
                    activeNotifications.add(notificationListItem)
                }
            }
        }

        return activeNotifications
    }*/

    /*private fun getMatchingNotification(reminderItem: ReminderItem, stockData: StockData): NotificationListItem? {
        if(reminderItem.symbol == stockData.symbol){
            if(reminderItem.isRemindIfPriceHigher){
                if(stockData.latestPrice > reminderItem.remindAt){
                    return NotificationListItem(
                        symbol = stockData.symbol,
                        companyName = stockData.companyName,
                        message = getString(R.string.priceOverText,reminderItem.remindAt,stockData.latestPrice),
                        isDown = false,
                        dateString = System.currentTimeMillis(),
                        recent = true
                    )
                }
            }
            else{
                if(stockData.latestPrice < reminderItem.remindAt){
                    return NotificationListItem(
                        symbol = stockData.symbol,
                        companyName = stockData.companyName,
                        message = getString(R.string.priceUnderText,reminderItem.remindAt,stockData.latestPrice),
                        isDown = true,
                        dateString = System.currentTimeMillis(),
                        recent = true
                    )
                }
            }
        }

        return null
    }*/

    private fun onGetNotifications(activeNotifications: ArrayList<NotificationListItem>){
        if(activeNotifications.isNotEmpty()) {
            sendNotification(activeNotifications)
            saveToDatabase(activeNotifications)
        }
    }

    private fun saveToDatabase(activeNotifications: ArrayList<NotificationListItem>) {
        val database = Room.databaseBuilder(application, NotificationListDatabase::class.java,"notification-list").build()
        database.notificationListItemDao().insertAll(activeNotifications)
        database.close()
    }

    private fun sendNotification(activeNotifications: ArrayList<NotificationListItem>) {
        val pendingIntent: PendingIntent = getPendingIntent()
        createNotificationChannel(this)

        val notificationContent = getNotificationContent(activeNotifications)

        val builder = NotificationCompat.Builder(this, "1")
                        .setSmallIcon(R.drawable.icon_no_background)
                        .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.icon_base))
                        .setContentTitle(getString(R.string.notificationTitle))
                        .setContentText(notificationContent)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setDefaults(Notification.DEFAULT_ALL)

        with(NotificationManagerCompat.from(this)) {
            this.notify(1, builder.build()) //this is always 1, for easy tracking
        }


    }

    private fun getPendingIntent(): PendingIntent {
        val intentStart = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(this, 0, intentStart, 0)
    }

    private fun createNotificationChannel(context: Context?) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "stockNotification"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotificationContent(activeNotifications: ArrayList<NotificationListItem>): String {
        if(activeNotifications.size==1){
            val item = activeNotifications.first()
            return item.symbol + " : " + item.message
        }
        else{
            val symbolList = ArrayList<String>()
            var symbolConcat = ""
            activeNotifications.forEach { if(!symbolList.contains(it.symbol)) symbolList.add(it.symbol) }

            symbolList.forEach {symbolString ->
                symbolConcat += "$symbolString("
                var notificationCount = 0
                activeNotifications.forEach { notificationListItem ->
                    if(symbolString == notificationListItem.symbol)
                        notificationCount++
                }
                symbolConcat += "$notificationCount), "
            }

            return "Alert for: $symbolConcat"
        }
    }

}

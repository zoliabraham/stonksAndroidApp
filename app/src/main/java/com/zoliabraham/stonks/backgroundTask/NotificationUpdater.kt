package com.zoliabraham.stonks.backgroundTask

import android.content.Context
import androidx.room.Room
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.data.StockData
import com.zoliabraham.stonks.data.notificationList.NotificationListItem
import com.zoliabraham.stonks.data.reminder.ReminderItem
import com.zoliabraham.stonks.data.reminder.ReminderListDatabase
import com.zoliabraham.stonks.network.StockDataListDownloader
import kotlin.reflect.KFunction1

class NotificationUpdater(val context: Context,val onFinishedCallback: KFunction1<ArrayList<NotificationListItem>, Unit>) {
    private var reminderList: List<ReminderItem>

    init {
        val database = Room.databaseBuilder(context, ReminderListDatabase::class.java,"reminder-list").build()
        reminderList = database.reminderListDao().getAll()
        val stockList = convertReminderDataToStockData(reminderList)
        StockDataListDownloader(stockList, ::onStockDataDownloaded, true) //run blocking, bc it already runs on a background thread, if not, service could be terminated
        database.close()
    }

    private fun convertReminderDataToStockData(list: List<ReminderItem>): ArrayList<StockData> {
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

    }

    private fun onStockDataDownloaded(data: ArrayList<StockData>){
        val activeNotifications = getActiveNotifications(data)
        onFinishedCallback(activeNotifications)
    }

    private fun getActiveNotifications(data: ArrayList<StockData>): ArrayList<NotificationListItem>{
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
    }

    private fun getMatchingNotification(reminderItem: ReminderItem, stockData: StockData): NotificationListItem? {
        if(reminderItem.symbol == stockData.symbol){
            if(reminderItem.isRemindIfPriceHigher){
                if(stockData.latestPrice > reminderItem.remindAt){
                    return NotificationListItem(
                        symbol = stockData.symbol,
                        companyName = stockData.companyName,
                        message = context.getString(R.string.priceOverText,reminderItem.remindAt,stockData.latestPrice),
                        isDown = false,
                        dateString = System.currentTimeMillis(),
                        recent = true,
                        remindAt = reminderItem.remindAt
                    )
                }
            }
            else{
                if(stockData.latestPrice < reminderItem.remindAt){
                    return NotificationListItem(
                        symbol = stockData.symbol,
                        companyName = stockData.companyName,
                        message = context.getString(R.string.priceUnderText,reminderItem.remindAt,stockData.latestPrice),
                        isDown = true,
                        dateString = System.currentTimeMillis(),
                        recent = true,
                        remindAt = reminderItem.remindAt
                    )
                }
            }
        }

        return null
    }



}
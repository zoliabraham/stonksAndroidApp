package com.zoliabraham.stonks.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import com.google.gson.Gson
import com.zoliabraham.stonks.data.StockData
import com.zoliabraham.stonks.data.notificationList.NotificationListDatabase
import com.zoliabraham.stonks.data.notificationList.NotificationListItem
import com.zoliabraham.stonks.notificationListPackage.NotificationListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {
    private var notificationArray = ArrayList<NotificationListItem>()
    val adapter = NotificationListAdapter(notificationArray)
    var database: NotificationListDatabase

    init {
        database = Room.databaseBuilder(getApplication(), NotificationListDatabase::class.java,"notification-list").build()
        GlobalScope.launch(Dispatchers.IO) {
            notificationArray.clear()
            notificationArray.addAll(database.notificationListItemDao().getAll().reversed())

            GlobalScope.launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }

            database.close()
        }
    }


    fun onExit(){
        GlobalScope.launch(Dispatchers.IO) {
            database = Room.databaseBuilder(getApplication(), NotificationListDatabase::class.java,"notification-list").build()
            for(notification in notificationArray){
                notification.recent = false
                database.notificationListItemDao().update(notification)
            }
            database.close()
        }

    }

    fun getStockInJson(position: Int): String {
        val stock = StockData(null, symbol = notificationArray[position].symbol, companyName = notificationArray[position].companyName)

        return Gson().toJson(stock)
    }

    fun deleteAllNotification() {
        notificationArray.clear()
        adapter.notifyDataSetChanged()

        GlobalScope.launch(Dispatchers.IO) {
            database = Room.databaseBuilder(getApplication(), NotificationListDatabase::class.java,"notification-list").build()
            database.notificationListItemDao().deleteAll()
            database.close()
        }
    }


}
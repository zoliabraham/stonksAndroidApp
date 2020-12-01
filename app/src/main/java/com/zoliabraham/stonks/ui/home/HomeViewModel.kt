package com.zoliabraham.stonks.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.google.gson.Gson
import com.zoliabraham.stonks.data.NewsArticleData
import com.zoliabraham.stonks.data.StockData
import com.zoliabraham.stonks.data.notificationList.NotificationListDatabase
import com.zoliabraham.stonks.data.notificationList.NotificationListItem
import com.zoliabraham.stonks.homePackage.HomeNewsListAdapter
import com.zoliabraham.stonks.homePackage.HomeRecentListAdapter
import com.zoliabraham.stonks.network.ArticleDownloader
import com.zoliabraham.stonks.network.StockDataListDownloader
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val showNewsLoading = MutableLiveData<Boolean>().apply {
        value = true
    }

    val showRecentNotifications = MutableLiveData<Boolean>().apply {
        value = false
    }

    var articleList = ArrayList<NewsArticleData>()
        set(value) {
            field.addAll(value)
            showNewsLoading.value = value.isEmpty()
            if(value.isEmpty()){
                retryDownloadArticles()
            }
        }


    val newsAdapter = HomeNewsListAdapter(articleList)
    private lateinit var timer: Timer

    var notificationList = ArrayList<NotificationListItem>()
    var stockList = ArrayList<StockData>()
    val recentAdapter = HomeRecentListAdapter(notificationList, stockList)

    init {
        getRecentNotifications()
    }

    private fun retryDownloadArticles(){
        if(!this::timer.isInitialized) {
            timer = fixedRateTimer("timer", false, 0, 5 * 1000) {
                if (articleList.isEmpty()) {
                    ArticleDownloader(::onArticlesDownloaded)
                } else {
                    this.cancel()
                }
            }
        }
    }


    private fun onArticlesDownloaded(articles: ArrayList<NewsArticleData>){
        GlobalScope.launch(Dispatchers.Main) {
            articleList.addAll(articles)
            newsAdapter.notifyDataSetChanged()
            showNewsLoading.value = false
        }
    }

    private fun getRecentNotifications() {
        GlobalScope.launch(Dispatchers.IO) {
            val database = Room.databaseBuilder(getApplication(), NotificationListDatabase::class.java, "notification-list").build()
            var recentNotifications = database.notificationListItemDao().getAllRecent()
            if (recentNotifications.isNotEmpty()) {
                recentNotifications = groupNotificationBySymbol(recentNotifications)
                notificationList.addAll(recentNotifications)
                GlobalScope.launch(Dispatchers.Main) {
                    showRecentNotifications.value = true
                }
                downloadStockDataForNotificationList()
            }
            database.close()
        }
    }

    private fun downloadStockDataForNotificationList() {
        val stockList = ArrayList<StockData>()
        for (notificationListItem in notificationList) {
            stockList.add(StockData(id = null, symbol = notificationListItem.symbol))
        }
        StockDataListDownloader(stockList, ::onStockDataDownloaded)
    }

    private fun onStockDataDownloaded(data: ArrayList<StockData>){
        stockList.addAll(data)
        GlobalScope.launch(Dispatchers.Main) {
            recentAdapter.notifyDataSetChanged()
        }
    }

    private fun groupNotificationBySymbol(recentNotifications: List<NotificationListItem>): ArrayList<NotificationListItem> {
        val groupedList = ArrayList<NotificationListItem>()

        for (recentNotification in recentNotifications) { //ordered list, first match is the newest
            if(!containsWithSymbol(recentNotification, groupedList)) {
                groupedList.add(recentNotification)
            }
        }

        return groupedList
    }

    private fun containsWithSymbol(recentNotification: NotificationListItem, groupedList: java.util.ArrayList<NotificationListItem>): Boolean {
        for (notificationListItem in groupedList) {
            if(recentNotification.symbol == notificationListItem.symbol){
                return true
            }
        }
        return false
    }

    fun getStockDataByPosition(position: Int): String {
        val notification = notificationList[position]
        val stockData = StockData(id = null, symbol = notification.symbol, companyName = notification.companyName)
        return Gson().toJson(stockData)
    }


}
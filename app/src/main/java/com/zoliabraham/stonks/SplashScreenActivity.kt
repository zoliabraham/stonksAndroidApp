package com.zoliabraham.stonks

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.gson.Gson
import com.zoliabraham.stonks.backgroundTask.NotificationUpdater
import com.zoliabraham.stonks.data.NewsArticleData
import com.zoliabraham.stonks.data.notificationList.NotificationListDatabase
import com.zoliabraham.stonks.data.notificationList.NotificationListItem
import com.zoliabraham.stonks.network.ArticleDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private var articles = ""
    private var articleList = ArrayList<NewsArticleData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //downloadNewsArticles
        //checkNotifications

        fun downloadArticlesAsync() = GlobalScope.async {
            ArticleDownloader(::addArticles, true)
        }

        fun updateNotificationDataAsync() = GlobalScope.async {
            NotificationUpdater(this@SplashScreenActivity, ::saveNotificationToDatabase)
        }

        GlobalScope.launch(Dispatchers.IO) {
            downloadArticlesAsync().await()
            updateNotificationDataAsync().await()
            closeSplashScreen()
        }

        /*Timer("SettingUp", false).schedule(1) {
            closeSplashScreen()
        }*/
    }

    private fun closeSplashScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("articleList", articles)
        val bundle = Bundle()
        bundle.putParcelableArrayList("articleArrayList", articleList)
        intent.putExtra("bundle",bundle)
        startActivity(intent)
        finish()
    }

    private fun saveNotificationToDatabase(activeNotifications: ArrayList<NotificationListItem>) {
        if(activeNotifications.isNotEmpty()) {
            val database = Room.databaseBuilder(application, NotificationListDatabase::class.java, "notification-list").build()
            database.notificationListItemDao().insertAll(activeNotifications)
            database.close()
        }
    }

    private fun addArticles(articles: ArrayList<NewsArticleData>){
        this.articles = Gson().toJson(articles)
        this.articleList = articles
    }
}
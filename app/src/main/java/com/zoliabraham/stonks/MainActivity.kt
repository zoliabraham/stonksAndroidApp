@file:Suppress("DEPRECATION")

package com.zoliabraham.stonks

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zoliabraham.stonks.backgroundTask.AlertReceiver
import com.zoliabraham.stonks.data.NewsArticleData
import com.zoliabraham.stonks.data.notificationList.NotificationListDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var articlesString: String
    lateinit var articleList: ArrayList<NewsArticleData>

    private lateinit var badge: BadgeDrawable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)

        badge = navView.getOrCreateBadge(R.id.navigation_notifications)
        setupNavControllerListener(navController)

        articlesString = intent.getStringExtra("articleList") ?: ""

        val bundle = intent.getBundleExtra("bundle")
        articleList = bundle?.getParcelableArrayList<NewsArticleData>("articleArrayList") as ArrayList<NewsArticleData>

        setUpToolbar()
    }

    private fun setUpToolbar() {
        //val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(application.getColor(R.color.colorPrimary))
        setSupportActionBar(toolbar)
    }

    fun enableBackButton(enable: Boolean){
        supportActionBar?.setDisplayHomeAsUpEnabled(enable)
        supportActionBar?.setDisplayShowHomeEnabled(enable)
    }

    override fun onResume() {
        super.onResume()
        cancelAlarm()
        hideNotificationBadge()
        updateNotification()
    }

    override fun onPause() {
        super.onPause()
        startAlarm()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startAlarm(){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)

        alarmManager?.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, 60000, pendingIntent)
    }

    private fun cancelAlarm(){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)

        alarmManager?.cancel(pendingIntent)

        //dismiss active notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }

    private fun showNotificationsBadge(i: Int = 0){
        badge.clearNumber()
        if(i!=0){
            badge.number = i
        }
        badge.isVisible = true
    }


    fun hideNotificationBadge(){
        badge.isVisible = false
    }

    private fun updateNotification(){
        GlobalScope.launch {
            val database = Room.databaseBuilder(this@MainActivity, NotificationListDatabase::class.java, "notification-list").build()
            val recentNotifications = database.notificationListItemDao().getAllRecent()
            if (recentNotifications.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.Main) {
                    showNotificationsBadge()
                }
            }
            database.close()
        }
    }

    fun toggleFullScreen(isEnabled: Boolean){
        if(isEnabled)
        {
            appBarLayout.visibility = View.GONE
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }
        }
        else
        {
            appBarLayout.visibility = View.VISIBLE
            window.apply {
                addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                statusBarColor = ContextCompat.getColor(context, R.color.white)
            }
        }
    }

    private fun setupNavControllerListener(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            title = when(destination.id){
                R.id.navigation_home -> getString(R.string.title_home)
                R.id.navigation_stocklist -> getString(R.string.title_stocklist)
                R.id.navigation_notifications -> getString(R.string.title_notifications)
                else -> ""
            }
        }
    }
}

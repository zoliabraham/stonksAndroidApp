package com.zoliabraham.stonks.data.notificationList

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NotificationListItem::class], version = 1,  exportSchema = false)
abstract class NotificationListDatabase : RoomDatabase() {
    abstract fun notificationListItemDao(): NotificationListDao
}
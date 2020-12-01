package com.zoliabraham.stonks.data.reminder

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ReminderItem::class], version = 1)
abstract class ReminderListDatabase : RoomDatabase() {
    abstract fun reminderListDao(): ReminderListDao
}
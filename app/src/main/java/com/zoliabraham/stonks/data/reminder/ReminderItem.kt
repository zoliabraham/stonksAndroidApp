package com.zoliabraham.stonks.data.reminder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminderlistitem")
data class ReminderItem(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "remindAt") var remindAt: Float = -1f,
    @ColumnInfo(name = "isRemindIfPriceHigher") var isRemindIfPriceHigher: Boolean = false
)
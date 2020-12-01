package com.zoliabraham.stonks.data.notificationList

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notificationlistitem")
data class NotificationListItem (
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "symbol") var symbol: String = "",
    @ColumnInfo(name = "companyName") var companyName: String = "",
    @ColumnInfo(name = "message") var message: String = "",
    @ColumnInfo(name = "isDown") var isDown: Boolean = true,
    @ColumnInfo(name = "dateString") var dateString: Long = 0L,
    @ColumnInfo(name = "recent") var recent: Boolean = false,
    @ColumnInfo(name = "remindAt") var remindAt: Float = -1F,
)
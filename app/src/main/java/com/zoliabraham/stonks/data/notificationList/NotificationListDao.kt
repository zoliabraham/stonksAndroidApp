package com.zoliabraham.stonks.data.notificationList

import androidx.room.*

@Dao
interface NotificationListDao {
    @Query("SELECT * FROM notificationlistitem")
    fun getAll(): List<NotificationListItem>

    @Query("SELECT * FROM notificationlistitem WHERE recent=1 ") //1=true, 0 = false
    fun getAllRecent(): List<NotificationListItem>

    @Insert
    fun insert(notificationListItem: NotificationListItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<NotificationListItem>)

    @Update
    fun update(notificationListItem: NotificationListItem)

    @Delete
    fun deleteItem(notificationListItem: NotificationListItem)

    @Query("DELETE FROM notificationlistitem")
    fun deleteAll()
}
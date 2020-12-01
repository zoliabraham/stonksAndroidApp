package com.zoliabraham.stonks.data.reminder

import androidx.room.*

@Dao
interface ReminderListDao {
    @Query("SELECT * FROM reminderlistitem")
    fun getAll(): List<ReminderItem>

    @Insert
    fun insert(reminderItem: ReminderItem): Long

    @Update
    fun update(reminderItem: ReminderItem)

    @Delete
    fun deleteItem(reminderItem: ReminderItem)

    @Query("Select * From reminderlistitem WHERE symbol = :symbol")
    fun getRemindersWithSymbol(symbol: String): List<ReminderItem>
}
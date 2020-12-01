package com.zoliabraham.stonks.data.stockList

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zoliabraham.stonks.data.StockData

@Database(entities = [StockData::class], version = 1)
abstract class StockListDatabase : RoomDatabase() {
    abstract fun stocklistItemDao(): StockListDao
}
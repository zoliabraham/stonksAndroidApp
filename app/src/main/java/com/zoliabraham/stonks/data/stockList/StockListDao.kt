package com.zoliabraham.stonks.data.stockList

import androidx.room.*
import com.zoliabraham.stonks.data.StockData

@Dao
interface StockListDao {
    @Query("SELECT * FROM stocklistitem")
    fun getAll(): List<StockData>

    @Insert
    fun insert(stockData: StockData): Long

    @Update
    fun update(stockData: StockData)

    @Delete
    fun deleteItem(stockData: StockData)

    @Query("DELETE FROM stocklistitem")
    fun deleteAll()
}


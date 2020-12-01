package com.zoliabraham.stonks.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocklistitem", ignoredColumns = ["extraEnabled"])
data class StockData(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "companyName") var companyName: String = "",
    @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "latestPrice") var latestPrice: Float = -1f,

    @ColumnInfo(name = "change") var change: Float = 0.0f,
    @ColumnInfo(name = "changePercent") var changePercent: Float = 0.0f,

    @ColumnInfo(name = "open") var open: Float = 0.0f,
    @ColumnInfo(name = "previousClose") var previousClose: Float = 0.0f,

    @ColumnInfo(name = "week52High") var week52High: Float = 0.0f,
    @ColumnInfo(name = "week52Low") var week52Low: Float = 0.0f,

    @ColumnInfo(name = "high") var high: Float = 0.0f,
    @ColumnInfo(name = "low") var low: Float = 0.0f,

    @ColumnInfo(name = "marketCap") var marketCap: Long = 0,
    @ColumnInfo(name = "peRatio") var peRatio: Float = 0.0f,


) {
    var extraEnabled : Boolean = false
}
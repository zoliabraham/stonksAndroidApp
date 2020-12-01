package com.zoliabraham.stonks.stocklistpackage

import com.zoliabraham.stonks.data.StockData
import com.zoliabraham.stonks.data.stockList.StockListDatabase
import com.zoliabraham.stonks.network.StockDataListDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StockListManager(val onListLoaded: () -> Unit, val onListUpdated: () -> Unit) {
    var stockList = ArrayList<StockData>()
    lateinit var database: StockListDatabase


    fun loadStockList() {
        //val list = SharedPreferencesManager().getSavedStockList(context)
        GlobalScope.launch(Dispatchers.IO) {
            val list = database.stocklistItemDao().getAll()
            list.forEach { //dont display false values
                it.latestPrice = -1.0f
            }
            stockList.clear()
            stockList.addAll(list)
            onListLoaded()

            refreshData()
        }
    }

    fun refreshData(){
        StockDataListDownloader(stockList, ::updateStockData)
    }

    fun saveStockList() {
        GlobalScope.launch(Dispatchers.IO) {
            database.stocklistItemDao().deleteAll()
            stockList.forEach {
                database.stocklistItemDao().insert(it)
            }
            //SharedPreferencesManager().saveStockList(context, stockList)
        }
    }

    private fun updateStockData(data: ArrayList<StockData>){
        stockList.clear()
        stockList.addAll(data)
        onListUpdated()
    }
}
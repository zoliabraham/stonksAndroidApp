package com.zoliabraham.stonks.ui.stocklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.google.gson.Gson
import com.zoliabraham.stonks.data.SearchData
import com.zoliabraham.stonks.data.StockData
import com.zoliabraham.stonks.data.stockList.StockListDatabase
import com.zoliabraham.stonks.stocklistpackage.SearchListAdapter
import com.zoliabraham.stonks.stocklistpackage.SearchManager
import com.zoliabraham.stonks.stocklistpackage.StockListAdapter
import com.zoliabraham.stonks.stocklistpackage.StockListManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class StockListViewModel(application: Application) : AndroidViewModel(application) {
    val stockListManager = StockListManager( ::stockListLoaded, ::stockListUpdated)
    val database: StockListDatabase = Room.databaseBuilder(getApplication(),StockListDatabase::class.java,"stock-list").build()
    val stockListAdapter = StockListAdapter(stockListManager.stockList)

    private var searchList = ArrayList<SearchData>()
    val searchListAdapter = SearchListAdapter(searchList)

    private var searchManager: SearchManager = SearchManager(stockListManager.stockList)

    init {
        stockListManager.database = database
        stockListManager.loadStockList()
        startUpdateTimer()
    }

    private val searchText = MutableLiveData<String>()

    fun searchText(item: String) {
        var searchResults = ArrayList<SearchData>()
        if(item != "") {
            searchText.value = item
            searchResults = searchManager.getSearchResult(item)
        }
        searchList.clear()
        searchList.addAll(searchResults)
        searchListAdapter.notifyDataSetChanged()
    }

    fun searchItemClicked(position :Int){
        if(searchList.size>0) {
            val selected = searchList[position]
            val stockData = StockData(companyName = selected.name, symbol = selected.symbol, latestPrice = -1.0f)
            stockListManager.stockList.add(stockData)
            stockListAdapter.notifyItemInserted(stockListManager.stockList.lastIndex)

            stockListManager.refreshData()
        }
    }


    override fun onCleared() {
        super.onCleared()
        stockListManager.saveStockList()
    }


    private fun stockListLoaded(){
        GlobalScope.launch(Dispatchers.Main) {
            stockListAdapter.notifyDataSetChanged()
        }
    }

    private fun stockListUpdated(){
        GlobalScope.launch(Dispatchers.Main) {
            stockListAdapter.notifyDataSetChanged()
        }
    }

    fun getStockDataJson(position: Int): String {
        val toParse = stockListManager.stockList[position]
        return Gson().toJson(toParse)
    }

    private fun startUpdateTimer() {
        val refreshIntervalInSecs:Long = 30
        Timer().scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                stockListManager.refreshData()
            }

        },0,refreshIntervalInSecs*1000)
    }
}

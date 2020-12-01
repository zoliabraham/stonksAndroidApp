package com.zoliabraham.stonks.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zoliabraham.stonks.data.KeyStore
import com.zoliabraham.stonks.data.StockData
import java.lang.Exception

class StockDataListDownloader(private val stockDataList: ArrayList<StockData>, val onFinished: (data: ArrayList<StockData>) -> Unit, private val runBlocking: Boolean = false) {
    private val symbols: ArrayList<String> = ArrayList()

    init {
        stockDataList.forEach {symbols.add(it.symbol)}
        downloadStockData()
    }

    private fun downloadStockData(){
        if(symbols.size>0) {
            if(!runBlocking)
                NetworkManager.getStandardResponse(buildUrl(), ::stockDataDownloaded)
            else
                NetworkManager.getStandardResponseBlocking(buildUrl(), ::stockDataDownloaded)
        }
    }

    private fun buildUrl(): String {
        var symbolsString = ""
        symbols.forEach { symbolsString += if(it != symbols.last())"${it}," else it }

        //iex cloud
        return "https://${KeyStore.iexApiUrl}/stable/stock/market/batch?symbols=${symbolsString}&types=quote&token=${KeyStore.iexApiKey}"
    }

    private fun stockDataDownloaded(jsonString: String){
        onFinished(processJson(jsonString))
    }

    private fun processJson(jsonString: String): ArrayList<StockData> {
        return try {
            val map:Map<String,Map<String, StockData>> = Gson().fromJson(jsonString,object: TypeToken<Map<String,Map<String, StockData>>>(){}.type ) //json to map

            val arrayList = ArrayList<StockData>()

            for((_, value) in map){
                arrayList.add(value["quote"] as StockData)
            }

            arrayList
        } catch (e: Exception){
            ArrayList(stockDataList)
        }

    }

}
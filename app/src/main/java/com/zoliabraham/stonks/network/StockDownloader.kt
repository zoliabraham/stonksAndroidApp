package com.zoliabraham.stonks.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zoliabraham.stonks.data.ChartDataPoint
import com.zoliabraham.stonks.data.KeyStore
import com.zoliabraham.stonks.data.NamedInterval
import com.zoliabraham.stonks.data.StockData
import kotlinx.coroutines.*

class StockDownloader(private val stockData: StockData, private val interval: NamedInterval, val onFinished: (stockData: StockData, chart: ArrayList<ChartDataPoint>) -> Unit) {

    init {
        downloadStockData()
    }

    private fun downloadStockData(){
        NetworkManager.getStandardResponse(buildUrl(), ::stockDataDownloaded)
    }

    private fun buildUrl(): String {
        return "https://${KeyStore.iexApiUrl}/stable/stock/market/batch?symbols=${stockData.symbol}&types=quote,chart&range=${interval.value}&token=${KeyStore.iexApiKey}"
    }

    private fun stockDataDownloaded(jsonString: String){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val array = splitIntoParsable(jsonString)
                onFinished(processQuote(array[0]), processChart(array[1]))
            }
            catch (e: Exception){
                val errorData = StockData(id = null, symbol = stockData.symbol, companyName = stockData.companyName)
                val errorChart = ArrayList<ChartDataPoint>()
                onFinished(errorData, errorChart)
                Log.e("stockDownloader", "error parsing json\n ${e.printStackTrace()}")
            }

        }

    }

    private fun splitIntoParsable(jsonString: String): ArrayList<String> {
        //split json into 2 separately parsable json strings

        //remove all json annotations and add back necessary ones
        val chartString = jsonString.substring(jsonString.indexOf("["), jsonString.indexOf("]")) + "]"
        val quoteSting = jsonString.substring(jsonString.indexOf("\"quote\":{")+8, jsonString.indexOf("}", jsonString.indexOf("\"quote\":{"))) + "}"

        val list = ArrayList<String>()
        list.add(quoteSting)
        list.add(chartString)
        return list
    }

    private fun processQuote(jsonString: String): StockData {
        return Gson().fromJson(jsonString, StockData::class.java )
    }

    private fun processChart(chartListStr: String): ArrayList<ChartDataPoint> {
        val sType = object : TypeToken<ArrayList<ChartDataPoint>>() {}.type
        return Gson().fromJson(chartListStr, sType)
    }
}

package com.zoliabraham.stonks.stocklistpackage

import com.zoliabraham.stonks.data.SearchData
import com.zoliabraham.stonks.data.StockData
import com.zoliabraham.stonks.network.SearchDataDownloader
import java.util.*
import kotlin.collections.ArrayList

class SearchManager(private val alreadyUsed: ArrayList<StockData>) {
    private var searchableList: ArrayList<SearchData> = ArrayList()

    init {
        val downloader = SearchDataDownloader(this)
        downloader.downloadSearchResults()
    }

    fun getSearchResult(searched: String): ArrayList<SearchData> { //TODO removeFromUIThread
        val resultList = ArrayList<SearchData>()

        val searchedStr = searched.toLowerCase(Locale.ROOT)


        for ( it in searchableList){
            if(it.name.toLowerCase(Locale.ROOT).contains(searchedStr) || it.symbol.toLowerCase(Locale.ROOT).contains(searchedStr)){
                if(!isUsed(it))
                resultList.add(it)
            }
            if(resultList.size>20){
                break
            }
        }

        //Log.e("search", "search results: ${resultList.size}")
        return resultList
    }

    private fun isUsed(searchData: SearchData): Boolean {
        for (stockData in alreadyUsed){
            if(stockData.symbol == searchData.symbol){
                return true
            }
        }
        return false
    }

    fun setSearchableList(list : ArrayList<SearchData>){
        this.searchableList = list
    }


}
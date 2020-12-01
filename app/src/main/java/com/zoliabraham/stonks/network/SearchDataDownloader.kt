package com.zoliabraham.stonks.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zoliabraham.stonks.data.SearchData
import com.zoliabraham.stonks.stocklistpackage.SearchManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchDataDownloader(private var searchManager: SearchManager) {
    private val url = "https://api.iextrading.com/1.0/ref-data/symbols"

    fun downloadSearchResults(){
        GlobalScope.launch(Dispatchers.IO) {
            NetworkManager.getStandardResponse(url, ::onResult)
        }
    }

    private fun onResult(result: String){
        val searchableList = processResponse(result)
        if (searchableList != null) {
            searchManager.setSearchableList(ArrayList(searchableList))
        }
    }

    private fun processResponse(json: String): ArrayList<SearchData>? {
        return try {
            val sType = object : TypeToken<ArrayList<SearchData>>() {}.type
            Gson().fromJson<ArrayList<SearchData>>(json, sType)
        } catch (e: Exception){
            ArrayList()
        }

    }


}
package com.zoliabraham.stonks.ui.stock

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.data.ChartDataPoint
import com.zoliabraham.stonks.data.NamedInterval
import com.zoliabraham.stonks.data.StockData
import com.zoliabraham.stonks.network.StockDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class StockViewModel(application: Application) : AndroidViewModel(application) {
    val stockData = MutableLiveData<StockData>()
    val chartData = MutableLiveData<LineData>()
    val showLoading = MutableLiveData<Boolean>().apply { value = true }

    private var selectedChartInterval: NamedInterval = NamedInterval.DAY1

    private fun convertFromJson(stockDataJson: String): StockData {
        return Gson().fromJson(stockDataJson, StockData::class.java)
    }

    fun setStockData(stockDataJson: String){
        stockData.value = convertFromJson(stockDataJson)
        refreshStockData()
    }

    private fun refreshStockData(){
        if (stockData.value != null) StockDownloader(stockData.value!!, selectedChartInterval, ::updateData)
    }

    private fun updateData(stockData: StockData, chart: ArrayList<ChartDataPoint>){
        val lineData = getLineData(chart)
        GlobalScope.launch(Dispatchers.Main) {
            if(stockData.latestPrice != -1f) {
                this@StockViewModel.stockData.value = stockData
                chartData.value = lineData
                showLoading.value = false
            }
            else{
                Toast.makeText(getApplication(), getApplication<Application>().getString(R.string.error_downloading_stock_data), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun getLineData(chart: ArrayList<ChartDataPoint>): LineData {
        val entries = ArrayList<Entry>()

        var i = 0.0f
        chart.forEach {
            entries.add(Entry(i, it.close))
            i+=1.0f
        }

        val dataSet = LineDataSet(entries, stockData.value?.symbol)

        //style diagram
        dataSet.setDrawCircles(false)
        dataSet.setDrawFilled(true)
        dataSet.setDrawValues(false)
        dataSet.color = getApplication<Application>().getColor(R.color.colorPrimary)
        dataSet.fillColor = getApplication<Application>().getColor(R.color.colorPrimary)

        /*if(chart.size<30)
            dataset.mode = LineDataSet.Mode.CUBIC_BEZIER //this looks good, but not for a stock app
        else
            dataset.mode = LineDataSet.Mode.LINEAR*/

        return LineData(dataSet)
    }

    fun getTabListener(): TabLayout.OnTabSelectedListener {
        return object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabClicked(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        }
    }

    private fun tabClicked(tab: TabLayout.Tab?) {
        if (tab != null) {
            when (tab.text.toString().toLowerCase(Locale.ROOT)){
                getApplication<Application>().getString(R.string.days1string) -> selectedChartInterval = NamedInterval.DAY1
                getApplication<Application>().getString(R.string.days5string) -> selectedChartInterval = NamedInterval.DAY5
                getApplication<Application>().getString(R.string.months1string) -> selectedChartInterval = NamedInterval.MONTH1
                getApplication<Application>().getString(R.string.years1string) -> selectedChartInterval = NamedInterval.YEAR1
                getApplication<Application>().getString(R.string.years5string) -> selectedChartInterval = NamedInterval.YEAR5
            }
        }
        refreshStockData()
    }


}
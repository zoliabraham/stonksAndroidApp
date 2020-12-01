package com.zoliabraham.stonks.ui.stock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.zoliabraham.stonks.MainActivity
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.ui.stock.reminderDialog.ReminderDialogFragment
import kotlinx.android.synthetic.main.fragment_stock.*

class StockFragment : Fragment() {

    private lateinit var viewModel: StockViewModel

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        viewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        return inflater.inflate(R.layout.fragment_stock, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpChart()
        setupObservers()

        val args = arguments
        viewModel.setStockData(args?.get("stockDataJson") as String)
        stockTabLayout.addOnTabSelectedListener(viewModel.getTabListener())

        showReminderDialogButton.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 -> viewModel.stockData.value?.symbol?.let { it2 -> ReminderDialogFragment(it2).show(it1, ReminderDialogFragment.TAG) } }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).enableBackButton(true)
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).enableBackButton(false)
    }

    private fun setupObservers() {
        viewModel.stockData.observe(viewLifecycleOwner,{
            val color = ResourcesCompat.getColor(resources, if(it.change<0) R.color.stockDownRed else  R.color.colorPrimary, null) //without theme
            stockCodeSymbolText.text = it.symbol
            stockCompanyName.text = it.companyName

            stockPrice.text = getString(R.string.roundTo2Digits).format(it.latestPrice)
            stockPriceChange.text = getString(R.string.roundTo2Digits).format(it.change)
            stockPercentChange.text = getString(R.string.percent).format(it.changePercent)

            stockPrice.setTextColor(color)
            stockPriceChange.setTextColor(color)
            stockPercentChange.setTextColor(color)

            stockOpen.text = getString(R.string.roundTo2Digits).format(it.open)
            stockPrevClose.text = getString(R.string.roundTo2Digits).format(it.previousClose)
            stock52high.text = getString(R.string.roundTo2Digits).format(it.week52High)
            stock52low.text = getString(R.string.roundTo2Digits).format(it.week52Low)
            stockHigh.text = getString(R.string.roundTo2Digits).format(it.high)
            stockLow.text = getString(R.string.roundTo2Digits).format(it.low)
            stockMarketCap.text = getWithPostFix(it.marketCap)
            stockPpE.text = getString(R.string.roundTo2Digits).format(it.peRatio)
        })

        viewModel.showLoading.observe(viewLifecycleOwner, {
            toggleShowLoading(it)
        })

        viewModel.chartData.observe(viewLifecycleOwner, {
            stockLineChart.data = it
            stockLineChart.invalidate()
            stockLineChart.animateY(500, Easing.EaseInOutCirc)
        })
    }

    private fun toggleShowLoading(isLoading: Boolean) {
        if(!isLoading){
            rootLayout.transitionToEnd()
        }
        else{
            rootLayout.transitionToStart()
        }
    }

    private fun setUpChart(){
        //style diagram
        stockLineChart.description = Description().apply { text = ""}
        stockLineChart.isAutoScaleMinMaxEnabled = false
        stockLineChart.axisLeft.isEnabled = true
        stockLineChart.axisRight.isEnabled = true
        stockLineChart.xAxis.isEnabled = false
        stockLineChart.legend.isEnabled = false

        stockLineChart.setScaleEnabled(false)
        stockLineChart.isDragEnabled = false
    }

    private fun getWithPostFix(it: Long): String {
        var postfix = ""
        var value = it.toFloat()
        val postfixes = listOf("K","M","B","T")
        for(i in 0..4){
            if(value/1000 > 1){
                value /= 1000
                postfix = postfixes[i]
            }
        }

        return "%.2f".format(value)+postfix
    }






}
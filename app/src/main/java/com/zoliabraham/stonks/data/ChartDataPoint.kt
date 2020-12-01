package com.zoliabraham.stonks.data

data class ChartDataPoint(
    var date: String = "",
    var open: Float,
    var close: Float,
    var high: Float,
    var low: Float,
    var change: Float,
    var changePercent: Float,
)
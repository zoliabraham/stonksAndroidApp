package com.zoliabraham.stonks.data

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsArticleData(
    var author: String = "",
    var title: String = "",
    var date: String = "",
    var url: String = "",
    var imgUrl: String = "",
    var imgBitmap: Bitmap = Bitmap.createBitmap(10,10,Bitmap.Config.ARGB_8888),

    var keyWords: String = ""
) : Parcelable
package com.zoliabraham.stonks.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException


class NetworkManager {

    /*fun downloadBitmap(url: String, onFinCallback: (bitmap: Bitmap) -> Unit){
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .method("GET", null)
            .url(url)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("bitmap_download", "bitmap download failed: \n $e")
            }

            override fun onResponse(call: Call, response: Response) {
                GlobalScope.launch(Dispatchers.IO) {
                    if (response.body != null) {
                        val bitmap = BitmapFactory.decodeStream(response.body!!.byteStream())
                        onFinCallback(bitmap)
                    }
                }
            }
        })
    }*/

    companion object{
        fun getStandardResponse(url: String, onFinCallback: (responseBody: String) -> Unit) { //onFinishCallback already exists in okhttp
            val okHttpClient = OkHttpClient()
            val request = Request.Builder()
                .method("GET", null)
                .url(url)
                .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFinCallback("")
                }

                override fun onResponse(call: Call, response: Response) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            onFinCallback(responseBody)
                        }
                    }
                }
            })
        }


        fun getStandardResponseBlocking(url: String, onFinCallback: (responseBody: String) -> Unit){
            try {
                val okHttpClient = OkHttpClient()
                val request = Request.Builder()
                    .method("GET", null)
                    .url(url)
                    .build()
                val response = okHttpClient.newCall(request).execute()
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    onFinCallback(responseBody)
                }
            } catch (e: Exception){
                Log.e("networkManager", "${e.printStackTrace()}")
            }
        }


    }



}
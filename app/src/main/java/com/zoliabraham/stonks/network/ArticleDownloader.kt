package com.zoliabraham.stonks.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.zoliabraham.stonks.data.KeyStore
import com.zoliabraham.stonks.data.NewsArticleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class ArticleDownloader(val onFinished: (articles: ArrayList<NewsArticleData>) -> Unit, runBlocking: Boolean = false) {

    init {
        if (runBlocking) {
            downloadArticlesBlocking()
        } else {
            downloadArticles()
        }
    }

    private fun downloadArticles() {
        NetworkManager.getStandardResponse(buildUrl(), ::onArticlesDownloaded)
    }

    private fun downloadArticlesBlocking() {
        NetworkManager.getStandardResponseBlocking(buildUrl(), ::onArticlesDownloaded)
    }

    private fun buildUrl(): String {
        return "https://${KeyStore.newsApiUrl}/v2/everything?q=${getKeyWords()}&apiKey=${KeyStore.newsApiKey}"
    }

    private fun getKeyWords(): String {
        return "stock"
    }

    private fun onArticlesDownloaded(jsonString: String) {

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val type = object : TypeToken<ArrayList<NewsArticleData>>() {}.type
                val gsonBuilder = getCustomGsonBuilder(type)
                val customGson = gsonBuilder.create()
                val articles = customGson.fromJson<ArrayList<NewsArticleData>>(jsonString, type)

                if (articles == null)
                    throw Exception("articles Null")
                else
                    onFinished(articles)

            } catch (e: Exception) {
                Log.e("articleDownloader", "error parsing article Json\n ${e.printStackTrace()}")
            }
        }


    }

    private fun getCustomGsonBuilder(type: Type?): GsonBuilder {
        return GsonBuilder().apply {

            registerTypeAdapter(type, JsonDeserializer { json, _, _ ->
                val jsonObject = json.asJsonObject
                val jsonArticleArray = jsonObject.get("articles").asJsonArray

                val articles = ArrayList<NewsArticleData>()

                for (articleStr in jsonArticleArray) {
                    val articleJson = articleStr as JsonObject
                    val articleData = NewsArticleData(
                        author = if (jsonNotNull(articleJson.get("author"))) {
                            articleJson.get("author").asString
                        } else {
                            ""
                        },
                        title = if (jsonNotNull(articleJson.get("title"))) {
                            articleJson.get("title").asString
                        } else {
                            ""
                        },
                        date = if (jsonNotNull(articleJson.get("publishedAt"))) {
                            articleJson.get("publishedAt").asString
                        } else {
                            ""
                        },
                        url = if (jsonNotNull(articleJson.get("url"))) {
                            articleJson.get("url").asString
                        } else {
                            ""
                        },
                        imgUrl = if (jsonNotNull(articleJson.get("urlToImage"))) {
                            articleJson.get("urlToImage").asString
                        } else {
                            ""
                        },
                        keyWords = getKeyWords(),
                    )

                    articles.add(articleData)
                }

                return@JsonDeserializer articles
            })
        }
    }

    private fun jsonNotNull(json: JsonElement): Boolean {
        return try {
            json.asString != "null"
        } catch (e: Exception) {
            false
        }
    }

}
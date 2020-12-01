package com.zoliabraham.stonks.homePackage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.data.NewsArticleData
import kotlinx.android.synthetic.main.home_news_item.view.*
import kotlin.math.min

class HomeNewsListAdapter(private val newsList : ArrayList<NewsArticleData>) : RecyclerView.Adapter<HomeNewsListAdapter.HomeNewsListViewHolder>() {
    private lateinit var itemClickListener: OnItemClickListener
    private val lengthLimit = 3

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    class HomeNewsListViewHolder(itemView: View, itemClickListener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeNewsListViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.home_news_item,parent,false)
        return HomeNewsListViewHolder(cardView, itemClickListener)
    }

    override fun onBindViewHolder(holder: HomeNewsListViewHolder, position: Int) {
        val item = newsList[position]

        holder.itemView.newsTitle.text = item.title
        holder.itemView.newsUrl.text = item.url
        holder.itemView.newsAuthor.text = item.author
    }

    override fun getItemCount(): Int {
        return min(newsList.size, lengthLimit) //show limited num of items
    }



}
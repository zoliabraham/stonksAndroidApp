package com.zoliabraham.stonks.homePackage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.data.StockData
import com.zoliabraham.stonks.data.notificationList.NotificationListItem
import kotlinx.android.synthetic.main.home_recent_item.view.*
import kotlin.math.min

class HomeRecentListAdapter(val notificationList: ArrayList<NotificationListItem>, val stockList: ArrayList<StockData>): RecyclerView.Adapter<HomeRecentListAdapter.HomeRecentListViewHolder>(){
    private lateinit var itemClickListener: OnItemClickListener
    private val lengthLimit = 2

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    class HomeRecentListViewHolder(itemView: View, itemClickListener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecentListViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.home_recent_item,parent,false)
        return HomeRecentListViewHolder(cardView, itemClickListener)
    }

    override fun onBindViewHolder(holder: HomeRecentListViewHolder, position: Int) {
        holder.itemView.homeRecentRemindAt.text = notificationList[position].remindAt.toString()
        holder.itemView.homeRecentSymbolText.text = notificationList[position].symbol


        val current = if(stockList.isNotEmpty() && stockList.size>=position && stockList[position].latestPrice!=-1F)
                {holder.itemView.context.getString(R.string.roundTo2Digits).format(stockList[position].latestPrice)}
                else
                    "..."
        //holder.itemView.homeRecentCurrentPrice.setText(current)
        holder.itemView.homeRecentCurrentPrice.setText(current)

    }

    override fun getItemCount(): Int {
        return min(notificationList.size, lengthLimit)
    }
}
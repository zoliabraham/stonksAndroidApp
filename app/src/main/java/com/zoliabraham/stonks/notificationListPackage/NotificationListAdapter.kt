package com.zoliabraham.stonks.notificationListPackage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.data.notificationList.NotificationListItem
import kotlinx.android.synthetic.main.notification_recyclerview_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationListAdapter(private val notificationsDataSet: ArrayList<NotificationListItem>): RecyclerView.Adapter<NotificationListAdapter.NotificationListViewHolder>(){
    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    class NotificationListViewHolder(view : View,  itemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.notification_recyclerview_item,parent,false)

        return NotificationListViewHolder(cardView, itemClickListener)
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        val item = notificationsDataSet[position]
        holder.itemView.symbolText.text = item.symbol
        holder.itemView.companyNameText.text = item.companyName
        holder.itemView.messageText.text = item.message

        val date = Date(item.dateString)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.ROOT)
        holder.itemView.dateText.text = format.format(date)

        //image
        val imgResource = if (item.isDown) R.drawable.ic_round_trending_up_24_red else R.drawable.ic_round_trending_up_24_green
        holder.itemView.iconImageView.setImageResource(imgResource)
        if(!item.isDown)
            holder.itemView.iconImageView.scaleY=1.0f
        else
            holder.itemView.iconImageView.scaleY=-1.0f

        holder.itemView.newNotificationText.visibility = if(item.recent) View.VISIBLE else View.GONE


    }

    override fun getItemCount() = notificationsDataSet.size

}
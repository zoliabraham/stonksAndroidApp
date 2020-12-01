@file:Suppress("SimplifiableCallChain")

package com.zoliabraham.stonks.stocklistpackage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.data.StockData
import kotlinx.android.synthetic.main.stocklist_recyclerview_item.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class StockListAdapter(private val stockDataSet: ArrayList<StockData>): RecyclerView.Adapter<StockListAdapter.StockListViewHolder>() {
    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class StockListViewHolder(view: View, itemClickListener: OnItemClickListener): RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockListViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.stocklist_recyclerview_item, parent, false)
        return StockListViewHolder(cardView, itemClickListener)
    }

    override fun onBindViewHolder(holder: StockListViewHolder, position: Int) {
        val item = stockDataSet[position]
        holder.itemView.symbolTextViewStockList.text = item.symbol
        holder.itemView.companyNameTextStockList.text = item.companyName.capitalizeWords()
        holder.itemView.currencyText.text = holder.itemView.context.getString(R.string.currencyName)

        val price = ((item.latestPrice * 100).roundToInt().toFloat() / 100)
        val priceText = if(price!=-1.0f) "%.2f".format(price) else "..."
        holder.itemView.currentPriceText.setText(priceText)

        val percent = ((item.changePercent * 100).roundToInt().toFloat() / 100)
        val percentText = if(price!=-1.0f) "%.2f".format(percent) + "%" else "..."
        holder.itemView.changePercentText.setText(percentText)
        /*holder.itemView.changePercentText.setTextColor(
            when {
                price == -1.0f -> holder.itemView.context.getColor(R.color.loadingTextColor)
                percent < 0 -> holder.itemView.context.getColor(R.color.stockDownRed)
                else -> holder.itemView.context.getColor(R.color.colorPrimary)
            }
        )*/
        (holder.itemView.changePercentText.nextView as TextView).setTextColor(
            when {
                price == -1.0f -> holder.itemView.context.getColor(R.color.loadingTextColor)
                percent < 0 -> holder.itemView.context.getColor(R.color.stockDownRed)
                else -> holder.itemView.context.getColor(R.color.colorPrimary)
            }
        )
        (holder.itemView.changePercentText.currentView as TextView).setTextColor(
            when {
                price == -1.0f -> holder.itemView.context.getColor(R.color.loadingTextColor)
                percent < 0 -> holder.itemView.context.getColor(R.color.stockDownRed)
                else -> holder.itemView.context.getColor(R.color.colorPrimary)
            }
        )

        when {
            item.extraEnabled -> {
                holder.itemView.stocklistItemExtrasContainer.visibility = View.VISIBLE
                holder.itemView.stockListItemMainContainer.visibility = View.GONE
            }
            else -> {
                holder.itemView.stocklistItemExtrasContainer.visibility = View.GONE
                holder.itemView.stockListItemMainContainer.visibility = View.VISIBLE
            }
        }

        holder.itemView.setOnLongClickListener {
            toggleExtra(getItemViewType(position))
            true
        }

        holder.itemView.stocklistItemRemoveButton.setOnClickListener {
            removeItem(getItemViewType(position))
        }
    }

    override fun getItemCount(): Int = this.stockDataSet.size

    private fun removeItem(position: Int) {
        stockDataSet.removeAt(position)
        //notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    private fun toggleExtra(position: Int){
        stockDataSet[position].extraEnabled = !stockDataSet[position].extraEnabled
        notifyDataSetChanged()
    }

    private fun String.capitalizeWords(): String = split(" ").map { it.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT) }.joinToString(" ")
}
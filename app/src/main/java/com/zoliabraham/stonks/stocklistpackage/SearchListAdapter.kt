@file:Suppress("SimplifiableCallChain", "SimplifiableCallChain")

package com.zoliabraham.stonks.stocklistpackage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.data.SearchData
import kotlinx.android.synthetic.main.search_recyclerview_item.view.*
import kotlinx.android.synthetic.main.search_recyclerview_item.view.searchCompanyNameText
import java.util.*
import kotlin.collections.ArrayList

class SearchListAdapter(private val searchListDataSet: ArrayList<SearchData>): RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder>(){
    private lateinit var itemClickListener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    class SearchListViewHolder(view: View, itemClickListener: OnItemClickListener): RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.search_recyclerview_item,parent,false)
        return SearchListViewHolder(cardView, itemClickListener)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        holder.itemView.searchSymbolText.text = searchListDataSet[position].symbol
        holder.itemView.searchCompanyNameText.text = searchListDataSet[position].name.toLowerCase(Locale.ROOT).capitalizeWords()
    }

    override fun getItemCount(): Int = this.searchListDataSet.size

    private fun String.capitalizeWords(): String = split(" ").map { it.capitalize(Locale.ROOT) }.joinToString(" ")


}
package com.zoliabraham.stonks.reminderDialogPackage

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.data.reminder.ReminderItem
import kotlinx.android.synthetic.main.reminder_recyclerview_item.view.*
import java.lang.Exception
import kotlin.reflect.KFunction1

class ReminderListAdapter(private val reminderDataSet: ArrayList<ReminderItem>, val updateItem: KFunction1<ReminderItem, Unit>, val deleteItem: KFunction1<ReminderItem, Unit>)
    : RecyclerView.Adapter<ReminderListAdapter.ReminderListViewHolder>(){


    class ReminderListViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderListViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.reminder_recyclerview_item,parent,false)

        return ReminderListViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ReminderListViewHolder, position: Int) {
        val reminderItem = reminderDataSet[position]

        val toCheck = if(reminderItem.isRemindIfPriceHigher) holder.itemView.reminderRadioButtonOver else  holder.itemView.reminderRadioButtonUnder
        toCheck.isChecked = true

        holder.itemView.reminderItemEditText.setText(if(reminderItem.remindAt != -1f) reminderItem.remindAt.toString() else "")


        holder.itemView.reminderItemEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Float = try {
                    p0.toString().toFloat()
                } catch (e: Exception) {
                    -1f
                }

                reminderItem.remindAt = price
                itemChanged(reminderItem)
            }

        })


        holder.itemView.reminderItemRadioGroup.setOnCheckedChangeListener{ _: RadioGroup, id: Int ->
            reminderItem.isRemindIfPriceHigher = id == R.id.reminderRadioButtonOver

            itemChanged(reminderItem)
        }

        holder.itemView.removeReminderButton.setOnClickListener {
            itemRemoved(reminderDataSet[position])
        }



    }

    override fun getItemCount() = reminderDataSet.size

    private fun itemRemoved(item: ReminderItem){
        reminderDataSet.remove(item)
        notifyDataSetChanged() //no need for animation

        deleteItem(item)
    }

    fun itemChanged(item: ReminderItem){
        updateItem(item)
    }

}
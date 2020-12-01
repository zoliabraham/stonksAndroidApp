package com.zoliabraham.stonks.ui.stock.reminderDialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.zoliabraham.stonks.data.reminder.ReminderItem
import com.zoliabraham.stonks.data.reminder.ReminderListDatabase
import com.zoliabraham.stonks.reminderDialogPackage.ReminderListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ReminderDialogViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var symbol: String
    val reminderList =  ArrayList<ReminderItem>()
    val database: ReminderListDatabase = Room.databaseBuilder(getApplication(), ReminderListDatabase::class.java,"reminder-list").build()
    val reminderListAdapter = ReminderListAdapter(reminderList, ::updateReminderItem, ::deleteReminderItem)
    val isListLoaded = MutableLiveData<Boolean>().apply {
        value = false
    }

    fun loadData(symbol: String) {
        this.symbol = symbol
        GlobalScope.launch(Dispatchers.IO) {
            onListLoaded(database.reminderListDao().getRemindersWithSymbol(symbol))
        }
    }

    private fun onListLoaded(list: List<ReminderItem>){
        GlobalScope.launch(Dispatchers.Main) {
            reminderList.addAll(list)
            reminderListAdapter.notifyDataSetChanged()
            isListLoaded.value = true
        }
    }



    fun addNewReminderItem(){

        val item = ReminderItem(null, symbol)
        reminderList.add(item)
        reminderListAdapter.notifyItemInserted(reminderList.lastIndex)


        insertItem(item)
    }

    private fun updateReminderItem(item: ReminderItem){
        GlobalScope.launch(Dispatchers.IO) {
            database.reminderListDao().update(item)
        }
    }

    private fun deleteReminderItem(item: ReminderItem){
        GlobalScope.launch(Dispatchers.IO) {
            database.reminderListDao().deleteItem(item)
        }
    }

    private fun insertItem(item: ReminderItem){
        GlobalScope.launch(Dispatchers.IO) {
            item.id = database.reminderListDao().insert(item)
        }
    }

}

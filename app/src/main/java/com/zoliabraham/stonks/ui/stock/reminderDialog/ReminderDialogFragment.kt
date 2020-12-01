package com.zoliabraham.stonks.ui.stock.reminderDialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.reminderDialogPackage.ReminderListAdapter
import kotlinx.android.synthetic.main.dialog_fragment_reminders.*

class ReminderDialogFragment(val symbol:String) : DialogFragment(){
    companion object {
        const val TAG = "ReminderDialogFragment"
    }

    private lateinit var reminderRecyclerView: RecyclerView
    private lateinit var reminderViewAdapter: ReminderListAdapter
    private lateinit var reminderViewManager: RecyclerView.LayoutManager


    private lateinit var viewModel: ReminderDialogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(ReminderDialogViewModel::class.java)
        viewModel.loadData(symbol)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return AlertDialog.Builder(requireContext(), R.style.ReminderDialogTheme)
            .setView(getContentView()).create()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(viewModel.database.isOpen)
            viewModel.database.close()
    }


    override fun onStart() {
        super.onStart()
        dialog?.placeholderEditText?.visibility  = View.GONE //https://stackoverflow.com/a/26400540/12559737 this is tricking android giving the dialog the proper flags, could be done manual
        dialog?.reminderDialogStockCodeSymbolText?.text = symbol
        setupRecyclerView()
        setupButtons()
    }

    @SuppressLint("InflateParams")
    private fun getContentView(): View {
        return LayoutInflater.from(context).inflate(R.layout.dialog_fragment_reminders, null)
    }

    private fun setupRecyclerView(){
        reminderViewManager = LinearLayoutManager(activity)
        reminderViewAdapter = viewModel.reminderListAdapter

        if(dialog!=null) {

            reminderRecyclerView = dialog!!.reminderDialogRecyclerView.apply {
                setHasFixedSize(false)
                layoutManager = reminderViewManager
                adapter = reminderViewAdapter
            }
            viewModel.isListLoaded.observe(this.activity as LifecycleOwner,{
                reminderRecyclerView.visibility = if(it) View.VISIBLE else View.GONE
            })
        }

    }

    private fun setupButtons() {
        dialog?.reminderDialogAddButton?.setOnClickListener {
            viewModel.addNewReminderItem()
            reminderRecyclerView.smoothScrollToPosition(viewModel.reminderList.lastIndex)
        }

        dialog?.closeDialogButton?.setOnClickListener {
            dialog?.onBackPressed()
        }

    }
}
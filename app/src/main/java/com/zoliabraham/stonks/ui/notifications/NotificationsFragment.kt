package com.zoliabraham.stonks.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zoliabraham.stonks.MainActivity
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.notificationListPackage.NotificationListAdapter
import kotlinx.android.synthetic.main.alert_dialog_delete.view.*
import kotlinx.android.synthetic.main.fragment_notifications.*


class NotificationsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: NotificationListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var notificationsViewModel: NotificationsViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        (activity as MainActivity).hideNotificationBadge()
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationsViewModel.onExit()
    }

    private fun setupRecyclerView(){
        viewManager = LinearLayoutManager(activity)
        viewAdapter = notificationsViewModel.adapter

        recyclerView = notificationRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewAdapter.notifyDataSetChanged()

        viewAdapter.setOnItemClickListener(object : NotificationListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                try {
                    openStock(notificationsViewModel.getStockInJson(position - 1))
                } catch (e : Exception){
                    Log.e("notificationFragment", "invalid id")
                }
            }

        })
    }

    fun openStock(stockDataJson: String){
        val action = NotificationsFragmentDirections.actionNavigationNotificationsToNavigationStock(stockDataJson)
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.notification_toolbar_menu, menu)


        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.deleteNotifications){
            openConfirmDeleteDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openConfirmDeleteDialog() {
        val contentView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_delete, null)
        val dialog = android.app.AlertDialog.Builder(requireContext(), R.style.ReminderDialogTheme).setView(contentView).show()
        dialog.setCanceledOnTouchOutside(false)
        contentView.alertDialogDismissButton.setOnClickListener {
            dialog.dismiss()
        }
        contentView.alertDialogConfirmButton.setOnClickListener {
            notificationsViewModel.deleteAllNotification()
            dialog.dismiss()
        }
    }

}
package com.zoliabraham.stonks.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.zoliabraham.stonks.MainActivity
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.homePackage.HomeNewsListAdapter
import com.zoliabraham.stonks.homePackage.HomeRecentListAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.fixedRateTimer


class HomeFragment : Fragment() {
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsViewAdapter: HomeNewsListAdapter
    private lateinit var newsViewManager: LinearLayoutManager

    private lateinit var recentRecyclerView: RecyclerView
    private lateinit var recentViewAdapter: HomeRecentListAdapter
    private lateinit var recentViewManager: LinearLayoutManager


    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        //viewModel.addArticles((activity as MainActivity).articlesString)
        viewModel.articleList = (activity as MainActivity).articleList

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        setupNewsRecyclerView()
        setupRecentRecyclerView()
    }

    private fun setupRecentRecyclerView() {
        recentViewManager = LinearLayoutManager(activity)
        recentViewAdapter = viewModel.recentAdapter

        recentRecyclerView = homeRecentRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = recentViewManager
            adapter = recentViewAdapter
        }

        //viewAdapter.notifyDataSetChanged()
        recentViewAdapter.setOnItemClickListener(object : HomeRecentListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                openStock(viewModel.getStockDataByPosition(position))
            }
        })
    }

    private fun setupNewsRecyclerView() {
        val scrollSpeed = 300f
        val topScrollSpeed = scrollSpeed / 3f

        newsViewManager = LinearLayoutManager(activity)
        newsViewAdapter = viewModel.newsAdapter

        newsRecyclerView = homeNewsRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = newsViewManager
            adapter = newsViewAdapter
        }

        //viewAdapter.notifyDataSetChanged()
        newsViewAdapter.setOnItemClickListener(object : HomeNewsListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                try {
                    openUrlInBrowser(viewModel.articleList[position].url)
                } catch (e: Exception) {
                    Log.e("homeFragment", "error, while opening url")
                }
            }

        })

        var timer = getTimer(scrollSpeed, topScrollSpeed)

        newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    timer.cancel()
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val currentPosition = newsViewManager.findFirstVisibleItemPosition()
                    val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(newsRecyclerView.context) {
                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                            return scrollSpeed / displayMetrics.densityDpi
                        }
                    }
                    linearSmoothScroller.targetPosition = currentPosition
                    newsViewManager.startSmoothScroll(linearSmoothScroller)
                    timer.cancel()
                    timer = getTimer(scrollSpeed, topScrollSpeed)
                }
            }
        })
    }

    private fun getTimer(scrollSpeed: Float, topScrollSpeed: Float): Timer {
        return fixedRateTimer("timer", false, 5 * 1000, 5 * 1000) {
            GlobalScope.launch(Dispatchers.Main) {
                val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(newsRecyclerView.context) {
                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                        return scrollSpeed / displayMetrics.densityDpi
                    }
                }

                val toTopSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(newsRecyclerView.context) {
                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                        return topScrollSpeed / displayMetrics.densityDpi
                    }
                }

                val currentPosition = newsViewManager.findFirstVisibleItemPosition()
                if (currentPosition == newsViewAdapter.itemCount - 1) {
                    //recyclerView.smoothScrollToPosition(0)
                    toTopSmoothScroller.targetPosition = 0
                    newsViewManager.startSmoothScroll(toTopSmoothScroller)
                } else {
                    linearSmoothScroller.targetPosition = currentPosition + 1
                    newsViewManager.startSmoothScroll(linearSmoothScroller)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.showNewsLoading.observe(viewLifecycleOwner, { showLoading ->
            if(showLoading){
                newsRecyclerView.visibility = View.GONE
                homeCircularProgressBar.visibility = View.VISIBLE
            }
            else{
                newsRecyclerView.visibility = View.VISIBLE
                viewModel.newsAdapter.notifyDataSetChanged()
                homeCircularProgressBar.visibility = View.INVISIBLE
                viewModel.newsAdapter.notifyDataSetChanged()
            }
        })

        viewModel.showRecentNotifications.observe(viewLifecycleOwner , { showNotifications ->
            if(showNotifications){
                homeNoRecentNotificationsText.visibility = View.GONE
            }
            else{
                homeNoRecentNotificationsText.visibility = View.VISIBLE
            }
        })
    }

    override fun onResume() {
        super.onResume()
        //(activity as AppCompatActivity).supportActionBar?.hide()
        (activity as MainActivity).toggleFullScreen(true)
    }

    override fun onPause() {
        super.onPause()
        //(activity as AppCompatActivity).supportActionBar?.show()
        (activity as MainActivity).toggleFullScreen(false)
    }

    fun openUrlInBrowser(url: String){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    fun openStock(stockDataJson: String){
        val action = HomeFragmentDirections.actionNavigationHomeToNavigationStock(stockDataJson)
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(action)
    }

}
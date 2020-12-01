package com.zoliabraham.stonks.ui.stocklist

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zoliabraham.stonks.R
import com.zoliabraham.stonks.stocklistpackage.SearchListAdapter
import com.zoliabraham.stonks.stocklistpackage.StockListAdapter
import kotlinx.android.synthetic.main.fragment_stocklist.*

class StockListFragment : Fragment() {
    private lateinit var stockRecyclerView: RecyclerView
    private lateinit var stockViewAdapter: StockListAdapter
    private lateinit var stockViewManager: RecyclerView.LayoutManager

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchViewAdapter: SearchListAdapter
    private lateinit var searchViewManager: LinearLayoutManager

    private lateinit var stockListViewModel: StockListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        stockListViewModel = ViewModelProvider(this).get(StockListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_stocklist, container, false)

        retainInstance=true
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        addStockButton.setOnClickListener {
            addStockButtonClicked()
        }

        setUpSearchEditText()
        setUpRecyclerViews()
        setUpMotionLayoutListener()
    }

    private fun setUpMotionLayoutListener() {
        stockListMotionLayout.addTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) { }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) { }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                showKeyboard()
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) { }


        })
    }

    private fun setUpRecyclerViews() {
        stockViewManager = LinearLayoutManager(activity)
        stockViewAdapter = stockListViewModel.stockListAdapter

        stockRecyclerView = stocklistRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = stockViewManager
            adapter = stockViewAdapter
        }
        stockViewAdapter.setOnItemClickListener(object : StockListAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                //stockListViewModel.stockItemClicked(position)
                if(!stockListViewModel.stockListManager.stockList[position].extraEnabled)
                    openStock(stockListViewModel.getStockDataJson(position))
            }
        })

        searchViewManager = LinearLayoutManager(activity)
        searchViewAdapter = stockListViewModel.searchListAdapter
        searchRecyclerView = searchListRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = searchViewManager
            adapter = searchViewAdapter
        }
        searchViewAdapter.setOnItemClickListener(object :SearchListAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                closeSearchBar()
                stockRecyclerView.scrollToPosition(stockListViewModel.stockListManager.stockList.lastIndex)
                stockListViewModel.searchItemClicked(position)
            }

        })
    }

    private fun addStockButtonClicked(){
        if(stockListMotionLayout.progress==0f) {
            openSearchBar()
            //activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        else{
            closeSearchBar()
            //activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }
    }

    private fun openSearchBar() {
        searchStockEditText.text.clear()
        stockListMotionLayout.transitionToEnd()
        searchStockEditText.isEnabled = true
        searchStockEditText.requestFocus()

        showKeyboard()
    }

    private fun showKeyboard() {
        val view = activity?.currentFocus
        val methodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        searchStockEditText.isActivated = true
        searchStockEditText.isPressed = true
    }

    fun closeSearchBar(){
        stockListMotionLayout.transitionToStart()
        searchStockEditText.isEnabled = false
        searchStockEditText.clearFocus()
    }

    private fun setUpSearchEditText() {
        searchStockEditText.addTextChangedListener( object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                stockListViewModel.searchText(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        }
        )
    }

    fun openStock(stockDataJson: String){
        val action = StockListFragmentDirections.actionNavigationStocklistToNavigationStock(stockDataJson)
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(action)
    }

}
package com.rmsoft.moneza.home.transactions_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.l4digital.fastscroll.FastScroller
import com.rmsoft.moneza.R
import com.rmsoft.moneza.util.DataPersistence
import com.rmsoft.moneza.util.MessageReadAll


class TransactionsListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transactions_list, container, false)
    }

    private lateinit var adapter2 : TransactionsAdapter
    private lateinit var rvContacts : RecyclerView
    private lateinit var fastScroller : FastScroller

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvContacts = view.findViewById<View>(R.id.list2) as RecyclerView
        fastScroller = view.findViewById(R.id.fast_scroller) as FastScroller

        refreshAdapter ()

        val mSwipeRefreshLayout = view.findViewById(R.id.swipe_container) as SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(Runnable {
            // mSwipeRefreshLayout.isRefreshing = true
        })

    }

    override fun onRefresh() {
        val mSwipeRefreshLayout = view?.findViewById(R.id.swipe_container) as SwipeRefreshLayout
        Log.i("TRANSACTION_FRAGMENT", "Refreshed")
        refreshAdapter ()
        mSwipeRefreshLayout.isRefreshing = false
    }

    private fun refreshAdapter ()
    {
        MessageReadAll(requireActivity(), true).readMessages(requireContext())

        val messages = DataPersistence(requireActivity()).find ()
        // MessageReadAll(requireActivity(), false).readMessages(requireContext())

        adapter2 = TransactionsAdapter(messages)
        // Attach the adapter to the recyclerview to populate items
        rvContacts.adapter = adapter2
        rvContacts.adapter?.notifyDataSetChanged()
        // Set layout manager to position the items
        rvContacts.layoutManager = LinearLayoutManager(context)
        // That's all!

        rvContacts.addItemDecoration(HeaderItemDecoration(rvContacts) { itemPosition ->
            if (itemPosition >= 0 && itemPosition < adapter2.itemCount) {
                messages[itemPosition].type == "DAY"
            } else false
        })

        fastScroller.setSectionIndexer(adapter2)
        fastScroller.attachRecyclerView(rvContacts)
    }


}

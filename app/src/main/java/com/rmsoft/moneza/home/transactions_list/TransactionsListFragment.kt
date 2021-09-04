package com.rmsoft.moneza.home.transactions_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.l4digital.fastscroll.FastScroller
import com.rmsoft.moneza.R
import com.rmsoft.moneza.StateMachine
import com.rmsoft.moneza.util.DataPersistence
import com.rmsoft.moneza.util.Message
import com.rmsoft.moneza.util.MessageReadAll


class TransactionsListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transactions_list, container, false)
    }

    private lateinit var adapter2 : TransactionsAdapter
    private lateinit var adapter3 : TransactionsAdapter
    private lateinit var ads : RecyclerView
    private lateinit var rvContacts : RecyclerView
    private lateinit var fastScroller : FastScroller

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ads = view.findViewById<View>(R.id.list3) as RecyclerView

        rvContacts = view.findViewById<View>(R.id.list2) as RecyclerView
        fastScroller = view.findViewById(R.id.fast_scroller) as FastScroller

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


        viewModel.searchQuery.observe(viewLifecycleOwner, Observer { query ->
            Log.i("onQueryTextChange", query)
            refreshAdapter (query)
        })

    }

    override fun onResume() {
        super.onResume()
        refreshAdapter ()
    }

    private val viewModel: StateMachine by activityViewModels()


    fun onItemClicked(item: Message) {
        viewModel.selectMessage(item)
    }


    override fun onRefresh() {
        val mSwipeRefreshLayout = view?.findViewById(R.id.swipe_container) as SwipeRefreshLayout
        Log.i("TRANSACTION_FRAGMENT", "Refreshed")
        reReadMessages ()
        refreshAdapter ()
        mSwipeRefreshLayout.isRefreshing = false
    }

    private fun reReadMessages () {
        Log.i("MessageReadAll", "Starting...")
        MessageReadAll(requireActivity(), true).readMessages(requireContext(), requireActivity())
        Log.i("MessageReadAll", "Done.")
    }

    private fun refreshAdapter (query: String = "") {

        Log.i("MessageReadAll", "Starting...")
        val messages = DataPersistence(requireActivity()).find (query)
        // MessageReadAll(requireActivity(), false).readMessages(requireContext())

        val ads = arrayOf(Message(), Message())

        ads[0].type = "AD"
        ads[0].subject = "company 1"
        ads[1].type = "AD"
        ads[1].subject = "company 2"

        if (messages.size > 3)
            messages.add(3, ads[0])
        else
            messages.add(ads[0])

        if (messages.size < 14)
            messages.add(ads[1])
        else
            messages.add(13, ads[1])

        val listener = object: OnItemClickListener {
            override fun onItemClick(item: Message?) {
                Log.i("onItemClick", item?.subject!!)
                onItemClicked (item)
            }
        }

        adapter2 = TransactionsAdapter(messages, listener)
        // Attach the adapter to the recyclerview to populate items
        rvContacts.adapter = adapter2
        rvContacts.adapter?.notifyDataSetChanged()
        adapter2.notifyDataSetChanged()
        // Set layout manager to position the items
        rvContacts.layoutManager = LinearLayoutManager(context)
        // That's all!

        for (x in 0 until rvContacts.itemDecorationCount)
            rvContacts.removeItemDecoration(rvContacts.getItemDecorationAt(x))

        rvContacts.addItemDecoration(HeaderItemDecoration(rvContacts) { itemPosition ->
            if (itemPosition >= 0 && itemPosition < messages.size)
                messages[itemPosition].type == "DAY"
            else
                false
        })

        fastScroller.setSectionIndexer(adapter2)
        fastScroller.attachRecyclerView(rvContacts)
        Log.i("MessageReadAll", "Done.")
    }


}

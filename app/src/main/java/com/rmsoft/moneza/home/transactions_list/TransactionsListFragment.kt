package com.rmsoft.moneza.home.transactions_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.rmsoft.moneza.R
import com.rmsoft.moneza.transaction_details.TransactionDetails
import com.rmsoft.moneza.util.MessageReadAll
import se.emilsjolander.stickylistheaders.StickyListHeadersListView


class TransactionsListFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        MessageReadAll(requireActivity()).readMessages(requireContext())

        return inflater.inflate(R.layout.fragment_transactions_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stickyList = view.findViewById(R.id.list) as StickyListHeadersListView
        val adapter: TransactionsAdapter? = TransactionsAdapter(requireContext())

        stickyList.adapter = adapter

        val intent = Intent(context, TransactionDetails::class.java)

        stickyList.setOnItemClickListener { parent, v, position, id -> startActivity(intent) }

        stickyList.setOnItemLongClickListener { parent, v, position, id ->
            startActivity(intent)
            true
        }






        // ...
        // Lookup the recyclerview in activity layout
        val rvContacts = view.findViewById<View>(R.id.list2) as RecyclerView
        // Initialize contacts
        val contacts = Contact.createContactsList(20)
        // Create adapter passing in the sample user data
        val adapter2 = ContactsAdapter(contacts)
        // Attach the adapter to the recyclerview to populate items
        rvContacts.adapter = adapter2
        // Set layout manager to position the items
        rvContacts.layoutManager = LinearLayoutManager(context)
        // That's all!

        rvContacts.addItemDecoration(HeaderItemDecoration(rvContacts) { itemPosition ->
            if (itemPosition >= 0 && itemPosition < adapter2.itemCount) {
                contacts[itemPosition].type == 0
            } else false
        })

        val fastScroller = view.findViewById(R.id.fast_scroller) as FastScroller
        fastScroller.setSectionIndexer(adapter2)
        fastScroller.attachRecyclerView(rvContacts)

    }




}

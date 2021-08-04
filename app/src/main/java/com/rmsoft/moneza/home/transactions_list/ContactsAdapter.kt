package com.rmsoft.moneza.home.transactions_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.rmsoft.moneza.R

class ContactsAdapter (private val mContacts: List<Contact>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>(), FastScroller.SectionIndexer
{
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView: TextView = itemView.findViewById(R.id.contact_name)
        val messageButton: Button = itemView.findViewById(R.id.message_button)
        val date: TextView = itemView.findViewById(R.id.date)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.layout_transaction_row, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ContactsAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val contact: Contact = mContacts.get(position)
        // Set item views based on your views and data model
        val textView = viewHolder.nameTextView
        textView.text = contact.name
        val button = viewHolder.messageButton
        button.text = if (contact.isOnline) "Message" else "Offline"
        button.isEnabled = contact.isOnline
        val date = viewHolder.date

        if (contact.type == 0) {
            date.visibility = View.VISIBLE
            button.visibility = View.GONE
            textView.visibility = View.GONE
        }
        else {
            date.visibility = View.GONE
            button.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE
        }


    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mContacts.size
    }

    override fun getSectionText(p0: Int): CharSequence {
        return p0.toString()
    }
}
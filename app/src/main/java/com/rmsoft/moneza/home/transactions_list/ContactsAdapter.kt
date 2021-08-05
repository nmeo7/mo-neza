package com.rmsoft.moneza.home.transactions_list

import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.rmsoft.moneza.R


class ContactsAdapter(private val mContacts: List<Contact>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), FastScroller.SectionIndexer
{
    private val TYPE_ONE = 0
    private val TYPE_TWO = 1

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            TYPE_ONE -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_transaction_row, parent, false)
                ViewHolderOne(view)
            }
            TYPE_TWO -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_date_row, parent, false)
                ViewHolderTwo(view)
            }
            else -> {
                throw RuntimeException("The type has to be ONE or TWO")
            }
        }


        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.layout_transaction_row, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            TYPE_ONE -> initLayoutOne(holder as ViewHolderOne, position)
            TYPE_TWO -> initLayoutTwo(holder as ViewHolderTwo, position)
            else -> {
            }
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mContacts.size
    }

    override fun getSectionText(p0: Int): CharSequence {
        return p0.toString()
    }


    // determine which layout to use for the row
    override fun getItemViewType(position: Int): Int {
        val item = mContacts[position]
        return when (item.type) {
            1 -> {
                TYPE_ONE
            }
            0 -> {
                TYPE_TWO
            }
            else -> {
                TYPE_ONE
            }
        }
    }


    private fun initLayoutOne(viewHolder: ViewHolderOne, position: Int) {
        // Get the data model based on position
        val contact: Contact = mContacts[position]
        // Set item views based on your views and data model
        val textView = viewHolder.nameTextView
        textView.text = contact.name
        val button = viewHolder.messageButton
        button.text = if (contact.isOnline) "Message" else "Offline"
        button.isEnabled = contact.isOnline
    }

    private fun initLayoutTwo(viewHolder: ViewHolderTwo, position: Int) {
        val contact: Contact = mContacts[position]

        val date = viewHolder.date
        date.text = contact.name
    }


    // Static inner class to initialize the views of rows
    internal class ViewHolderOne(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.contact_name)
        val messageButton: Button = itemView.findViewById(R.id.message_button)

    }

    internal class ViewHolderTwo(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)

    }
}
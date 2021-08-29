package com.rmsoft.moneza.home.transactions_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.rmsoft.moneza.R
import com.rmsoft.moneza.util.Message


class TransactionsAdapter(private val transactions: List<Message>, private val listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), FastScroller.SectionIndexer
{
    private val TYPE_ONE = 0
    private val TYPE_TWO = 1
    private val TYPE_AD = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            TYPE_ONE -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_transaction_row, parent, false)
                ViewHolderOne(view)
            }
            TYPE_AD -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_ad_row, parent, false)
                ViewHolderAd(view)
            }
            TYPE_TWO -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_date_row, parent, false)
                ViewHolderTwo(view)
            }
            else -> {
                throw RuntimeException("The type has to be ONE or TWO")
            }
        }
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            TYPE_ONE -> initLayoutOne(holder as ViewHolderOne, position)
            TYPE_TWO -> initLayoutTwo(holder as ViewHolderTwo, position)
            TYPE_AD -> initLayoutAd(holder as ViewHolderAd, position)
            else -> {
            }
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun getSectionText(p0: Int): CharSequence {
        return transactions[p0].getDay()
    }


    // determine which layout to use for the row
    override fun getItemViewType(position: Int): Int {
        val item = transactions[position]
        return when (item.type) {
            "DAY" -> {
                TYPE_TWO
            }
            "AD" -> {
                TYPE_AD
            }
            else -> {
                TYPE_ONE
            }
        }
    }


    private fun initLayoutOne(viewHolder: ViewHolderOne, position: Int) {
        // Get the data model based on position
        val message: Message = transactions[position]
        // Set item views based on your views and data model

        val subject = viewHolder.subject
        val amount = viewHolder.amount
        val fee = viewHolder.fee
        val fee1 = viewHolder.fee1
        val fee2 = viewHolder.fee2
        val balance = viewHolder.balance
        val time = viewHolder.time
        val type = viewHolder.type

        subject.text = message.subject
        amount.text = message.parseAmount(message.amount)
        fee.text = message.parseAmount(message.fee)
        balance.text = message.parseAmount(message.balance)
        time.text = message.getTime()
        type.text = message.type

        if (message.fee == 0)
        {
            fee.visibility = View.GONE
            fee1.visibility = View.GONE
            fee2.visibility = View.GONE
        }
        else
        {
            fee.visibility = View.VISIBLE
            fee1.visibility = View.VISIBLE
            fee2.visibility = View.VISIBLE
        }


        if (message.type == "RECEIVING" || message.type == "DEPOSIT")
            viewHolder.icon.setImageResource(R.drawable.ic_receive)
        else
            viewHolder.icon.setImageResource(R.drawable.ic_send)

        viewHolder.bind(message, listener)
    }

    private fun initLayoutTwo(viewHolder: ViewHolderTwo, position: Int) {
        val contact: Message = transactions[position]

        val date = viewHolder.date
        date.text = contact.subject
    }

    private fun initLayoutAd(viewHolder: ViewHolderAd, position: Int) {
        val contact: Message = transactions[position]

        val ad = viewHolder.ad
        ad.text = contact.subject
    }

    val self = this

    // Static inner class to initialize the views of rows
    internal class ViewHolderOne(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subject: TextView = itemView.findViewById(R.id.subject)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val fee: TextView = itemView.findViewById(R.id.fee)
        val fee2: TextView = itemView.findViewById(R.id.fee2)
        val fee1: TextView = itemView.findViewById(R.id.fee1)
        val balance: TextView = itemView.findViewById(R.id.balance)
        val time: TextView = itemView.findViewById(R.id.time)
        val type: TextView = itemView.findViewById(R.id.type)
        val icon: ImageView = itemView.findViewById(R.id.item_icon)

        private lateinit var transaction : Message

        fun bind(message: Message, listener: OnItemClickListener) {
            itemView.setOnClickListener { listener.onItemClick(message) }
        }
    }

    internal class ViewHolderTwo(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
    }

    internal class ViewHolderAd(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ad: TextView = itemView.findViewById(R.id.ad)
    }
}
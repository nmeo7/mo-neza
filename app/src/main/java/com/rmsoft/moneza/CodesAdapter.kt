package com.rmsoft.moneza

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.rmsoft.moneza.home.transactions_list.OnItemClickListener
import com.rmsoft.moneza.util.Message

class CodesAdapter (private val codes: List<CodesModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_codes_row, parent, false)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        initLayout(holder as ViewHolder, position)
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return codes.size
    }

    private fun initLayout(viewHolder: ViewHolder, position: Int) {
        val contact: CodesModel = codes[position]

        val date = viewHolder.subject
        date.text = contact.name

        val date2 = viewHolder.amount
        date2.text = contact.code
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount: TextView = itemView.findViewById(R.id.code)
        val subject: TextView = itemView.findViewById(R.id.name)
    }
}
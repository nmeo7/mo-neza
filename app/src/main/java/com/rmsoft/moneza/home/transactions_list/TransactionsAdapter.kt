package com.rmsoft.moneza.home.transactions_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.rmsoft.moneza.R
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter

class TransactionsAdapter(context: Context) : BaseAdapter(),
    StickyListHeadersAdapter {

    private val countries: Array<String> = context.resources.getStringArray(R.array.countries)
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return countries.size
    }

    override fun getItem(position: Int): Any {
        return countries[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder()
            convertView = inflater.inflate(R.layout.layout_transactions_list, parent, false)
            holder.text = convertView.findViewById<View>(R.id.text) as TextView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        holder.text!!.text = countries[position]
        return convertView!!
    }

    override fun getHeaderView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val holder: HeaderViewHolder
        if (convertView == null) {
            holder = HeaderViewHolder()
            convertView = inflater.inflate(R.layout.layout_transaction_header, parent, false)
            holder.text = convertView.findViewById<View>(R.id.text1) as TextView
            convertView.tag = holder
        } else {
            holder = convertView.tag as HeaderViewHolder
            //set header text as first char in name
        }
        val headerText = ">>>" + countries[position].subSequence(0, 2)[1]
        holder.text!!.text = headerText
        return convertView!!
    }

    override fun getHeaderId(position: Int): Long {
        //return the first character of the country as ID because this is what headers are based upon
        return countries[position].subSequence(0, 2)[1].toLong()
    }

    internal inner class HeaderViewHolder {
        var text: TextView? = null
    }

    internal inner class ViewHolder {
        var text: TextView? = null
    }

}

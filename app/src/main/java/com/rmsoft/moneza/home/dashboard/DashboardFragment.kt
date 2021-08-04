package com.rmsoft.moneza.home.dashboard


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rmsoft.moneza.R


class DashboardFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    private fun parseMoney (it : List<Int>) : String
    {
        val ret = try {
            it.first()
        } catch (e : Exception) {
            0
        }

        if (ret >= 1000000)
        {
            val m = (ret / 1000000).toString()
            var k = ((ret / 1000) % 1000).toString()
            if (k.length == 1) k = "00$k"
            if (k.length == 2) k = "0$k"
            var u = (ret % 1000).toString()
            if (u.length == 1) u = "00$u"
            if (u.length == 2) u = "0$u"

            return "$m $k $u"
        }

        if (ret >= 1000)
        {
            val k = (ret / 1000).toString()
            var u = (ret % 1000).toString()
            if (u.length == 1) u = "00$u"
            if (u.length == 2) u = "0$u"

            return "$k $u"
        }

        return ret.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val commission = view.findViewById<TextView>(R.id.dash_commission)
        val balance = view.findViewById<TextView>(R.id.dash_balance)
        val deposit = view.findViewById<TextView>(R.id.dash_deposit)
        val payments = view.findViewById<TextView>(R.id.dash_payments)
        val received = view.findViewById<TextView>(R.id.dash_received)
        val sent = view.findViewById<TextView>(R.id.dash_sent)
        val total = view.findViewById<TextView>(R.id.dash_total)
        val withdrawals = view.findViewById<TextView>(R.id.dash_withdraws)

        val savings = view.findViewById<TextView>(R.id.dash_savings)
        val spendings = view.findViewById<TextView>(R.id.dash_spendings)
        val starting = view.findViewById<TextView>(R.id.dash_starting)
        val closing = view.findViewById<TextView>(R.id.dash_closing)
        val fee = view.findViewById<TextView>(R.id.dash_fee)
    }
}

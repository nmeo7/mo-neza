package com.rmsoft.moneza.home.dashboard


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.db.williamchart.slidertooltip.SliderTooltip
import com.db.williamchart.view.LineChartView
import com.google.android.material.datepicker.MaterialDatePicker
import com.rmsoft.moneza.R
import com.rmsoft.moneza.util.DataPersistence
import com.rmsoft.moneza.util.Message
import java.io.*
import java.util.*


class DashboardFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    private fun parseMoney(it: List<Int>) : String
    {
        val ret = try {
            it.first()
        } catch (e: Exception) {
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

        val persistance = DataPersistence(requireActivity())

        deposit.text = persistance.aggregates("DEPOSIT")
        payments.text = persistance.aggregates("PAYMENT")
        received.text = persistance.aggregates("RECEIVING")
        sent.text = persistance.aggregates("TRANSFER")
        withdrawals.text = persistance.aggregates("WITHDRAW")

        total.text = persistance.aggregates("*")
        commission.text = persistance.aggregates("*", "fee")

        val sp = persistance.aggregates("PAYMENT", "amount", 14).replace(" ", "").toInt() +
                persistance.aggregates("TRANSFER", "amount", 14).replace(" ", "").toInt() +
                persistance.aggregates("WITHDRAW", "amount", 14).replace(" ", "").toInt()

        val sv = persistance.aggregates("DEPOSIT", "amount", 14).replace(" ", "").toInt() +
                persistance.aggregates("RECEIVING", "amount", 14).replace(" ", "").toInt()

        savings.text = Message().parseAmount(sv)
        spendings.text = Message().parseAmount(sp)
        // val starting = view.findViewById<TextView>(R.id.dash_starting)
        // val closing = view.findViewById<TextView>(R.id.dash_closing)
        fee.text = persistance.aggregates("*", "fee", 14)

        balance.text = persistance.balance()
        starting.text = persistance.balance(14)
        closing.text = persistance.balance()

        val days = persistance.days()

        for (x in days["sp"]!!)
            Log.i("SPENDINGS", x.toString())

        // val intentContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        // requireActivity().startActivityForResult(intentContact, 5 /*PICK_CONTACT*/)

        populateChart(days["sp"]!!)


        // val dtp = DatePickerDialog.newInstance{ datePickerDialog: DatePickerDialog, i: Int, i1: Int, i2: Int -> }
        // dtp.accentColor = Color.BLACK
        // dtp.show(requireFragmentManager(), "")

        val now = Calendar.getInstance()
        var dpd = DatePickerDialog.newInstance(
            this,
            now[Calendar.YEAR],
            now[Calendar.MONTH],
            now[Calendar.DAY_OF_MONTH]
        )
        // dpd.show( requireActivity().fragmentManager, "Datepickerdialog")

        val builder = MaterialDatePicker.Builder.dateRangePicker()
        // val now = Calendar.getInstance()
        builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
        val picker = builder.build()
        picker.show(activity?.supportFragmentManager!!, picker.toString())

        picker.addOnNegativeButtonClickListener {  }
        picker.addOnPositiveButtonClickListener { Log.i("TAG", "The selected date range is ${it.first} - ${it.second}")}


    }

    fun populateChart(lineSet: MutableList<Pair<String, Float>>)
    {
        val lineChart = view?.findViewById(R.id.lineChart) as LineChartView

        lineChart.gradientFillColors =
                intArrayOf(
                    Color.parseColor("#81FFFFFF"),
                    Color.TRANSPARENT
                )
        lineChart.animation.duration = 1000L
        lineChart.tooltip =
                SliderTooltip().also {
                    it.color = Color.WHITE
                }
        /*
        lineChart.onDataPointTouchListener = { index, _, _ ->
            lineChartValue.text =
                lineSet.toList()[index]
                    .second
                    .toString()
        }*/
        lineChart.animate(lineSet)
    }

    override fun onDateSet(
        view: DatePickerDialog?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int,
        yearEnd: Int,
        monthOfYearEnd: Int,
        dayOfMonthEnd: Int
    ) {

    }
}

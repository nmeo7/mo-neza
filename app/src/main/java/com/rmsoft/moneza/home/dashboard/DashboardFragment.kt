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
import com.github.anastr.speedviewlib.ImageSpeedometer
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.components.Section
import com.google.android.material.datepicker.MaterialDatePicker
import com.leinardi.android.speeddial.SpeedDialView
import com.rmsoft.moneza.R
import com.rmsoft.moneza.util.DataPersistence
import com.rmsoft.moneza.util.Message
import io.github.farshidroohi.ChartEntity
import io.github.farshidroohi.LineChart
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

    fun updateDashboard (from: Long, to: Long)
    {
        val commission = view?.findViewById<TextView>(R.id.dash_commission)!!
        val balance = view?.findViewById<TextView>(R.id.dash_balance)!!
        val deposit = view?.findViewById<TextView>(R.id.dash_deposit)!!
        val payments = view?.findViewById<TextView>(R.id.dash_payments)!!
        val received = view?.findViewById<TextView>(R.id.dash_received)!!
        val sent = view?.findViewById<TextView>(R.id.dash_sent)!!
        val total = view?.findViewById<TextView>(R.id.dash_total)!!
        val withdrawals = view?.findViewById<TextView>(R.id.dash_withdraws)!!

        val savings = view?.findViewById<TextView>(R.id.dash_savings)!!
        val spendings = view?.findViewById<TextView>(R.id.dash_spendings)!!
        val starting = view?.findViewById<TextView>(R.id.dash_starting)!!
        val closing = view?.findViewById<TextView>(R.id.dash_closing)!!
        val fee = view?.findViewById<TextView>(R.id.dash_fee)!!

        val persistance = DataPersistence(requireActivity())

        deposit.text = persistance.aggregates("DEPOSIT", "amount", from, to)
        payments.text = persistance.aggregates("PAYMENT", "amount", from, to)
        received.text = persistance.aggregates("RECEIVING", "amount", from, to)
        sent.text = persistance.aggregates("TRANSFER", "amount", from, to)
        withdrawals.text = persistance.aggregates("WITHDRAW", "amount", from, to)

        total.text = persistance.aggregates("*", "amount", from, to)
        commission.text = persistance.aggregates("*", "fee", from, to)

        val sp = persistance.aggregates("PAYMENT", "amount", from, to).replace(" ", "").toInt() +
                persistance.aggregates("TRANSFER", "amount", from, to).replace(" ", "").toInt() +
                persistance.aggregates("WITHDRAW", "amount", from, to).replace(" ", "").toInt()

        val sv = persistance.aggregates("DEPOSIT", "amount", from, to).replace(" ", "").toInt() +
                persistance.aggregates("RECEIVING", "amount", from, to).replace(" ", "").toInt()

        savings.text = Message().parseAmount(sv)
        spendings.text = Message().parseAmount(sp)
        // val starting = view.findViewById<TextView>(R.id.dash_starting)
        // val closing = view.findViewById<TextView>(R.id.dash_closing)
        fee.text = persistance.aggregates("*", "fee", from, to)

        balance.text = persistance.balance()
        starting.text = persistance.balance(from)
        closing.text = persistance.balance(to)

        val days = persistance.days()

        for (x in days["sp"]!!)
            Log.i("SPENDINGS", x.toString())

        // val intentContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        // requireActivity().startActivityForResult(intentContact, 5 /*PICK_CONTACT*/)


        val days_few = persistance.days(from, to)

        populateChart(days_few["sp"]!!)

        var sum_sp = 0F
        var sum_sv = 0F
        for (x in days_few["sp"]!!)
        {
            sum_sp += x.second
        }
        for (x in days_few["sv"]!!)
        {
            sum_sv += x.second
        }

        // sum_sp /= days_few["sp"]!!.size
        // sum_sv /= days_few["sv"]!!.size

        var num_sv = 0
        var num_sp = 0

        var max_sv = 0F
        var min_sv = 0F
        var max_sp = 0F
        var min_sp = 0F

        val sv_sorted = days["sv"]!!.sortedBy { it.second }
        val sp_sorted = days["sp"]!!.sortedBy { it.second }

        val speedometer1 = view?.findViewById<ImageSpeedometer>(R.id.speedView1)
        val speedometer2 = view?.findViewById<ImageSpeedometer>(R.id.speedView2)

        val size = sv_sorted.size
        val start = size * 2 / 5
        val end = size * 9 / 10

        Log.i("DATA", sv_sorted[end].second.toString())
        Log.i("DATA", sv_sorted[start].second.toString())
        Log.i("DATA", sp_sorted[end].second.toString())
        Log.i("DATA", sp_sorted[start].second.toString())
        Log.i("DATA", sum_sv.toString())
        Log.i("DATA", sum_sp.toString())

        val size2 = days_few.size

        speedometer1!!.maxSpeed = size2 * sv_sorted[end].second + 1
        speedometer1!!.minSpeed = size2 * sv_sorted[start].second
        speedometer2!!.maxSpeed = size2 * sp_sorted[end].second + 1
        speedometer2!!.minSpeed = size2 * sp_sorted[start].second

        if (sum_sv > size2 * sv_sorted[end].second)
            speedometer1!!.maxSpeed = sum_sv
        if (sum_sp > size2 * sp_sorted[end].second)
            speedometer2!!.maxSpeed = sum_sp

        if (sum_sv < size2 * sv_sorted[start].second)
            speedometer1!!.minSpeed = sum_sv
        if (sum_sp < size2 * sp_sorted[start].second)
            speedometer2!!.minSpeed = sum_sp

        speedometer1?.speedTo(sum_sv)
        speedometer2?.speedTo(sum_sp)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        updateDashboard (System.currentTimeMillis() - 30 * 3600 * 1000 * 24L, System.currentTimeMillis())

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

        val date_text = view.findViewById<TextView>(R.id.time_range)
        date_text.setOnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            // val now = Calendar.getInstance()
            builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
            val picker = builder.build()
            picker.show(activity?.supportFragmentManager!!, picker.toString())

            picker.addOnNegativeButtonClickListener {  }
            picker.addOnPositiveButtonClickListener {
                Log.i("TAG", "The selected date range is ${it.first} - ${it.second}")
                updateDashboard (it.first, it.second)
            }
        }
    }

    fun populateChart(lineSet: MutableList<Pair<String, Float>>)
    {
        val lineChart = view?.findViewById(R.id.lineChart) as LineChartView

        lineChart.gradientFillColors =
                intArrayOf(
                        Color.TRANSPARENT,
                    Color.TRANSPARENT
                )
        lineChart.animation.duration = 1000L

        /*
        lineChart.tooltip =
                SliderTooltip().also {
                    it.color = resources.getColor(R.color.colorPrimaryDark)
                }
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

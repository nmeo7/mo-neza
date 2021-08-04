package com.rmsoft.moneza.home.dashboard

import android.graphics.Color
import android.os.Bundle
import com.db.williamchart.slidertooltip.SliderTooltip
import com.db.williamchart.view.BarChartView
import com.db.williamchart.view.DonutChartView
import com.db.williamchart.view.HorizontalBarChartView
import com.db.williamchart.view.LineChartView

class PopulateGraphs(var lineChart: LineChartView, var barChart: BarChartView, var donutChart: DonutChartView, var horizontalBarChart: HorizontalBarChartView)
{
    fun onCreate() {
        /**
         * Line Chart
         */
        lineChart.gradientFillColors =
            intArrayOf(
                Color.parseColor("#81FFFFFF"),
                Color.TRANSPARENT
            )
        lineChart.animation.duration = animationDuration
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

        /**
         * Bar Chart
         */
        barChart.animation.duration = animationDuration
        barChart.animate(barSet)

        /**
         * Donut Chart
         */
        donutChart.donutColors = intArrayOf(
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#9EFFFFFF"),
            Color.parseColor("#8DFFFFFF")
        )
        donutChart.animation.duration = animationDuration
        donutChart.animate(donutSet)

        /**
         * Horizontal Bar Chart
         */
        horizontalBarChart.animation.duration = animationDuration
        horizontalBarChart.animate(horizontalBarSet)
    }

    companion object {
        private val lineSet = listOf(
            "label1" to 5f,
            "label2" to 4.5f,
            "label3" to 4.7f,
            "label4" to 3.5f,
            "label5" to 3.6f,
            "label6" to 7.5f,
            "label7" to 7.5f,
            "label8" to 10f,
            "label9" to 5f,
            "label10" to 6.5f,
            "label11" to 3f,
            "label12" to 4f
        )

        private val barSet = listOf(
            "JAN" to 4F,
            "FEB" to 7F,
            "MAR" to 2F,
            "MAY" to 2.3F,
            "APR" to 5F,
            "JUN" to 4F
        )

        private val horizontalBarSet = listOf(
            "PORRO" to 5F,
            "FUSCE" to 6.4F,
            "EGET" to 3F
        )

        private val donutSet = listOf(
            20f,
            80f,
            100f
        )

        private const val animationDuration = 1000L
    }
}
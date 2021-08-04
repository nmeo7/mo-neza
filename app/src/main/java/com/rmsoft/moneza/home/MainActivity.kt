package com.rmsoft.moneza.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.leinardi.android.speeddial.SpeedDialView
import com.rmsoft.moneza.R
import com.rmsoft.moneza.home.dashboard.DashboardFragment
import com.rmsoft.moneza.home.transactions_list.TransactionsListFragment
import eu.long1.spacetablayout.SpaceTabLayout


class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: SpaceTabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(ActionsFragment())
        fragmentList.add(DashboardFragment())
        fragmentList.add(TransactionsListFragment())

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        tabLayout = findViewById<SpaceTabLayout>(R.id.spaceTabLayout)

        tabLayout.initialize(
            viewPager, supportFragmentManager,
            fragmentList, savedInstanceState
        )

        val speedDialView = findViewById<SpeedDialView>(R.id.speedDial)
        speedDialView.inflate(R.menu.menu_speed_dial)
        speedDialView.visibility = View.GONE

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int,positionOffset: Float,positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
                if (position != 1) {
                    speedDialView.close()
                    speedDialView.visibility = View.GONE
                }
                else
                    speedDialView.visibility = View.VISIBLE
            }
        })

        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            speedDialView.close() // To close the Speed Dial with animation
            false
        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tabLayout.saveState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.one -> {
                // open qr
                true
            }
            R.id.four -> {
                true
            }
            R.id.five -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

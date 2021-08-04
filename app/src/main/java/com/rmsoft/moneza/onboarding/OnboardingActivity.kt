package com.rmsoft.moneza.onboarding

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
import com.rmsoft.moneza.MainActivity
import com.rmsoft.moneza.R
import com.rmsoft.moneza.util.CheckPrivileges
import java.util.*


class OnboardingActivity : AppCompatActivity() {
    private lateinit var screenPager: ViewPager
    var introViewPagerAdapter: OnboardingPagerAdapter ? = null
    lateinit var tabIndicator: TabLayout
    lateinit var btnNext: Button
    lateinit var btnGetStarted: Button
    var linearLayoutNext: LinearLayout? = null
    var linearLayoutGetStarted: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        if (restorePreData()) {
            val mainActivity = Intent(applicationContext, MainActivity::class.java)
            startActivity(mainActivity)
            finish()
        }
        setContentView(R.layout.activity_onboarding)
        btnNext = findViewById(R.id.btn_next)
        btnGetStarted = findViewById(R.id.btn_get_started)
        linearLayoutNext = findViewById(R.id.linear_layout_next)
        linearLayoutGetStarted = findViewById(R.id.linear_layout_get_started)
        tabIndicator = findViewById(R.id.tab_indicator)

        //Data
        val mList: MutableList<OnboardingItem> = ArrayList<OnboardingItem>()
        mList.add(
            OnboardingItem(
                "Ikaze!",
                "Genzura imikoreshereze ya\nMULLA zawe NEZA\nmu buryo bwa gihanga. Do it in style.",
                R.drawable.travel_page_one
            )
        )
        mList.add(
            OnboardingItem(
                "Ubudasa",
                "Wihendwa kandi ishyurira serivisi\nzikunogeye gusa!",
                R.drawable.travel_page_two
            )
        )
        mList.add(
            OnboardingItem(
                "Umutekano",
                "Hamwe na Mo Neza muratekanye!",
                R.drawable.travel_page_two
            )
        )
        mList.add(
            OnboardingItem(
                "Uburenganzira",
                "Twemerere gusoma ubutumwa bwa\nMoMo yanyu gusa!",
                R.drawable.travel_page_three
            )
        )

        //Setup viewPager
        screenPager = findViewById(R.id.screen_viewpager)
        introViewPagerAdapter = OnboardingPagerAdapter(this, mList)
        screenPager.adapter = introViewPagerAdapter

        //Setup tab indicator
        tabIndicator.setupWithViewPager(screenPager)

        //Button Next
        btnNext.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                screenPager.setCurrentItem(screenPager.currentItem + 1, true)
            }
        })
        tabIndicator.addOnTabSelectedListener(object : BaseOnTabSelectedListener<TabLayout.Tab?> {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == mList.size - 1) {
                    loadLastScreen()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val self = this

        //Button Get Started
        btnGetStarted.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                CheckPrivileges(self,self).requestAllPermissions()
            }
        })
    }

    private fun restorePreData(): Boolean {
        val preferences = applicationContext.getSharedPreferences(
            "myPrefs",
            Context.MODE_PRIVATE
        )
        return preferences.getBoolean("isIntroOpened", false)
    }

    private fun savePrefsData() {
        val preferences = applicationContext.getSharedPreferences(
            "myPrefs",
            Context.MODE_PRIVATE
        )
        val editor = preferences.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.apply()
    }

    private fun loadLastScreen() {
        linearLayoutNext!!.visibility = View.INVISIBLE
        linearLayoutGetStarted!!.visibility = View.VISIBLE
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            101 -> {
                val mainActivity = Intent(applicationContext, MainActivity::class.java)
                startActivity(mainActivity)
                savePrefsData()
                finish()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
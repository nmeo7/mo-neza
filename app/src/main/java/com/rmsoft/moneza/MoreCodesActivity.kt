package com.rmsoft.moneza

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.rmsoft.moneza.ui.main.SectionsPagerAdapter
import com.rmsoft.moneza.ui.main.SectionsPagerAdapter2

class MoreCodesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_codes)
        val sectionsPagerAdapter2 = SectionsPagerAdapter2(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager_more_codes)
        viewPager.adapter = sectionsPagerAdapter2
        val tabs: TabLayout = findViewById(R.id.tabs_more_codes)
        tabs.setupWithViewPager(viewPager)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setBackgroundDrawable( resources.getDrawable(R.drawable.toolbar_bg) )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}
package com.rmsoft.moneza

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewpager.widget.ViewPager
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.google.android.material.navigation.NavigationView
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment
import com.leinardi.android.speeddial.SpeedDialView
import com.rmsoft.moneza.home.ActionsFragment
import com.rmsoft.moneza.home.dashboard.DashboardFragment
import com.rmsoft.moneza.home.transactions_list.TransactionsListFragment
import com.rmsoft.moneza.util.CheckPrivileges
import com.rmsoft.moneza.util.DataPersistence
import com.rmsoft.moneza.util.Message
import com.tapadoo.alerter.Alerter
import eu.long1.spacetablayout.SpaceTabLayout
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var tabLayout: SpaceTabLayout

    private val viewModel: StateMachine by viewModels()
    var currentNumber = ""

    private val REQUEST_CODE_QR_SCAN = 101

    lateinit var viewPager: ViewPager

    fun notifySmsReceived(message: Message) {
        // Log.d("notifySmsReceived", "onReceive: $strMessage")

        Alerter.create(this)
                .setTitle("Kwishyura " + message.subject!!)
                .setText("Waba wifuza kuyihuza na kode $currentNumber?")
                .setBackgroundColorRes(R.color.colorPrimaryDark)
                .addButton("Ok", R.style.AlertButton) {
                    message.subjectNumber = currentNumber.replace(" ", "")
                    DataPersistence(this).save(message)
                    Log.i("Connect", message.toString())
                    Alerter.hide()
                }
                .setDuration(24000)
                .show()


    }

    fun onSmsReceived(message: Message) {
        // Log.d("notifySmsReceived", "onReceive: $strMessage")

        message.subjectNumber = currentNumber.replace(" ", "")
        DataPersistence(this).save(message)
        Log.i("Connect", message.toString())
    }

    fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentList = ArrayList<androidx.fragment.app.Fragment>()
        fragmentList.add(DashboardFragment())
        fragmentList.add(ActionsFragment())
        fragmentList.add(TransactionsListFragment())

        viewPager = findViewById<ViewPager>(R.id.viewPager)
        tabLayout = findViewById<SpaceTabLayout>(R.id.spaceTabLayout)

        tabLayout.initialize(
                viewPager, supportFragmentManager,
                fragmentList, savedInstanceState
        )

        val speedDialView = findViewById<SpeedDialView>(R.id.speedDial)
        speedDialView.inflate(R.menu.menu_speed_dial)
        speedDialView.visibility = View.GONE

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
                if (position != 2) {
                    speedDialView.close()
                    speedDialView.visibility = View.GONE
                } else
                    speedDialView.visibility = View.GONE
                // speedDialView.visibility = View.VISIBLE
            }
        })

        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            speedDialView.close() // To close the Speed Dial with animation
            false
        })

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.activity_main)
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_actions, R.id.nav_transactions_list, R.id.nav_dashboard), drawerLayout)
        val navController = findNavController(R.id.nav_host_fragment)

        setupActionBarWithNavController(navController, appBarConfiguration)

        /*
        setContentView(R.layout.activity_main2)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_actions, R.id.nav_transactions_list, R.id.nav_dashboard), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)*/

        setNavigationViewListener()

        viewModel.selectedMessage.observe(this, Observer { item ->
            viewPager.setCurrentItem(1, true)
            if (item != null) {
                if (item.subjectNumber != null)
                    currentNumber = item.subjectNumber!!
            }
        })

        // val messageReceiver = MessageReceiver(this)
        // val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        // registerReceiver(messageReceiver, filter)


        val now = Calendar.getInstance()
        var dpd = DatePickerDialog.newInstance(
            this,
            now[Calendar.YEAR],
            now[Calendar.MONTH],
            now[Calendar.DAY_OF_MONTH]
        )
        // dpd.show(fragmentManager, "Datepickerdialog")

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tabLayout.saveState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val myActionMenuItem = menu.findItem(R.id.action_search)
        val searchView = myActionMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                // Toast like print
                // UserFeedback.show("SearchOnQueryTextSubmit: $query")
                if (!searchView.isIconified) {
                    searchView.isIconified = true
                }
                myActionMenuItem.collapseActionView()
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                if (viewPager.currentItem != 2)
                    viewPager.setCurrentItem(2, true)
                viewModel.updateQuery(s!!)
                return false
            }
        })

        // val searchView = menu.findItem(R.id.action_search).actionView as SearchView

        val txtSearch = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        txtSearch.hint = "gushaka..."
        txtSearch.setHintTextColor(Color.DKGRAY)
        txtSearch.setTextColor(Color.BLACK)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.qr -> {
                // open qr

                if (!CheckPrivileges(this, this).requestCameraPermission()) {
                    val i = Intent(this@MainActivity, QrActivity::class.java)
                    startActivity(i)
                }
                true
            }
            R.id.action_search -> {
                if (viewPager.currentItem != 2)
                    viewPager.setCurrentItem(2, true)
                true
            }
            /*
            R.id.import_ -> {
                var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
                chooseFile.type = "text/plain"
                chooseFile = Intent.createChooser(chooseFile, "Choose a file")
                startActivityForResult(chooseFile, 3)
                true
            }*/
            /*
            R.id.export -> {
                val i = Intent(this@MainActivity, ShowQrActivity::class.java)
                startActivity(i)
                true
            }*/
            /*
            R.id.refresh -> {
                MessageReadAll(this, true).readMessages(this)

                true
            }
            R.id.reset -> {
                DataPersistence(this).reset()

                true
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_dashboard -> {
                DataPersistence(this).reset()
            }
        }

        Log.i("NavigationItemSelected", item.toString())
        //close navigation drawer
        val mDrawerLayout = findViewById<DrawerLayout>(R.id.activity_main)
        mDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setNavigationViewListener() {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        Log.d("THE_LOG", data.toString())

        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return

            val result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult")
            val ussdCode = result!!.substring(0, result.length - 1)
            val ussdCodeNew = ussdCode + Uri.encode("#")

            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))

            Log.d("THE_LOG", ussdCodeNew)

        }

        super.onActivityResult(requestCode, resultCode, data)
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
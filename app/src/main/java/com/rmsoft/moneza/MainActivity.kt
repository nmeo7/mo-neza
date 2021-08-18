package com.rmsoft.moneza

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.google.android.material.navigation.NavigationView
import com.leinardi.android.speeddial.SpeedDialView
import com.rmsoft.moneza.home.ActionsFragment
import com.rmsoft.moneza.home.dashboard.DashboardFragment
import com.rmsoft.moneza.home.transactions_list.TransactionsListFragment
import com.rmsoft.moneza.util.DataPersistence
import eu.long1.spacetablayout.SpaceTabLayout
import java.io.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var tabLayout: SpaceTabLayout

    private val viewModel: StateMachine by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentList = ArrayList<androidx.fragment.app.Fragment>()
        fragmentList.add(DashboardFragment())
        fragmentList.add(ActionsFragment())
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

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
                if (position != 2) {
                    speedDialView.close()
                    speedDialView.visibility = View.GONE
                } else
                    speedDialView.visibility = View.VISIBLE
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
        })


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
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.qr -> {
                // open qr
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
            R.id.export -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TITLE, "example.txt")

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.EMPTY)
                        }
                    }

                    startActivityForResult(intent, 4)
                }
                true
            }
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
        when (requestCode) {
            3 -> if (resultCode == RESULT_OK) {
                val fileUri = data?.data
                val filePath = fileUri?.path
                Log.i("filePath", filePath.toString())
                Log.i("filePath", fileUri.toString())

                val stringBuilder = StringBuilder()
                contentResolver.openInputStream(fileUri!!)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line)
                            line = reader.readLine()
                        }
                    }
                }
                Log.i("filePath_content", "content $stringBuilder")
            }

            4 -> if (resultCode == RESULT_OK) {
                val outputStream: OutputStream
                val fileUri = data?.data
                try {
                    outputStream = contentResolver.openOutputStream(fileUri!!)!!
                    val bw = BufferedWriter(OutputStreamWriter(outputStream))
                    bw.write("bison is bision")
                    bw.flush()
                    bw.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
package com.rmsoft.moneza.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.WriterException
import com.l4digital.fastscroll.FastScroller
import com.rmsoft.moneza.CodesAdapter
import com.rmsoft.moneza.CodesModel
import com.rmsoft.moneza.R
import com.rmsoft.moneza.home.transactions_list.HeaderItemDecoration
import com.rmsoft.moneza.home.transactions_list.OnItemClickListener
import com.rmsoft.moneza.home.transactions_list.TransactionsAdapter
import com.rmsoft.moneza.util.DataPersistence
import com.rmsoft.moneza.util.Message

/**
 * A placeholder fragment containing a simple view.
 */
class CodesListFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    private lateinit var adapter2 : CodesAdapter
    private lateinit var rvContacts : RecyclerView

    private var sectionNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
            sectionNumber = arguments?.getInt(ARG_SECTION_NUMBER) ?: 1
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_more_codes, container, false)
        rvContacts = root.findViewById<View>(R.id.list_codes) as RecyclerView

        return root
    }

    override fun onResume() {
        super.onResume()
        refreshAdapter ()
    }

    private fun refreshAdapter (query: Int = 0) {

        Log.i("MessageReadAll", "Starting...")
        val banks = listOf(
                CodesModel("BPR", "*150#"),
                CodesModel("Bank of Kigali", "*334#"),
                CodesModel("AB Bank", "*540#"),
                CodesModel("KCB", "*522#"),
                CodesModel("Equity Bank", "*555#"),
                CodesModel("Access Bank", "*903#"),
                CodesModel("ECOBANK", "*883#"),
                CodesModel("I&M", "*227#"),
                CodesModel("Bank of Africa", "*512#"),
                CodesModel("Cogebanque", "*505#"),
                CodesModel("Urwego Bank", "*501#"),
                CodesModel("GT Bank", "*600#")
        )

        val hotlines = listOf(
                CodesModel("Emergency", "112"),
                CodesModel("Ambulance Services", "912"),
                CodesModel("Traffic Accidents", "113"),
                CodesModel("Gender Based Violance", "3512"),
                CodesModel("Anti-Corruption", "997"),
                CodesModel("RIB", "166"),
                CodesModel("Babyl", "8111"),
                CodesModel("Urubuto", "7740")
        )

        val others = listOf(
                CodesModel("UPI", "*651#"),
                CodesModel("Ubudehe", "*909#"),
                CodesModel("KVCS", "*000#"),
                CodesModel("MoCash", "*000#"),
                CodesModel("Urubuto", "*775*1#")
        )

        val listener = object: OnItemClickListener {
            override fun onItemClick(item: Message?) {
                // Log.i("onItemClick", item?.subject!!)
                // onItemClicked (item)
            }
        }

        val codes = listOf(banks, hotlines, others)

        adapter2 = CodesAdapter(codes[sectionNumber - 1], listener)
        // Attach the adapter to the recyclerview to populate items
        rvContacts.adapter = adapter2
        rvContacts.adapter?.notifyDataSetChanged()
        adapter2.notifyDataSetChanged()
        // Set layout manager to position the items
        rvContacts.layoutManager = LinearLayoutManager(context)
        // That's all!
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): CodesListFragment {
            return CodesListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
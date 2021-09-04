package com.rmsoft.moneza.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rmsoft.moneza.R
import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
 * A placeholder fragment containing a simple view.
 */
class ScanQrFragment : Fragment(), ZXingScannerView.ResultHandler {

    private lateinit var pageViewModel: PageViewModel
    private var mScannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_show_qr, container, false)
        // val textView: TextView = root.findViewById(R.id.section_label)
        // pageViewModel.text.observe(viewLifecycleOwner, Observer<String> {
            // textView.text = it
        // })


        mScannerView = root.findViewById<ZXingScannerView>(R.id.scannerView)

        return root
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
        fun newInstance(sectionNumber: Int): ScanQrFragment {
            return ScanQrFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()           // Stop camera on pause
    }

    override fun handleResult(rawResult: com.google.zxing.Result?) {
        // Do something with the result here
        Log.v("THE_LOG", rawResult!!.text) // Prints scan results
        Log.v(
                "THE_LOG",
                rawResult.barcodeFormat.toString()
        ) // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        // mScannerView!!.resumeCameraPreview(this)


        val result = rawResult!!.text
        val ussdCode = result!!.substring(0, result.length - 1)
        val ussdCodeNew = ussdCode + Uri.encode("#")

        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))
    }
}
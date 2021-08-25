package com.rmsoft.moneza

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import me.dm7.barcodescanner.zxing.ZXingScannerView


class SimpleScannerActivity : Activity(), ZXingScannerView.ResultHandler {
    private var mScannerView: ZXingScannerView? = null

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        mScannerView = ZXingScannerView(this)   // Programmatically initialize the scanner view
        setContentView(mScannerView)                // Set the scanner view as the content view
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
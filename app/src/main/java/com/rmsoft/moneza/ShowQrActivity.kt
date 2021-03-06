package com.rmsoft.moneza

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.zxing.WriterException
import me.dm7.barcodescanner.zxing.ZXingScannerView


class ShowQrActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    var bitmap: Bitmap? = null
    var qrgEncoder: QRGEncoder? = null

    lateinit var dataEdt : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_qr)

        // initializing all variables.
        dataEdt = findViewById<EditText>(R.id.idEdt)
        // val generateQrBtn = findViewById<Button>(R.id.idBtnGenerateQR)

        dataEdt.addTextChangedListener(textWatcher)





        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        val tm = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        var mPhoneNumber = if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ""
        } else
            tm.line1Number

        Log.i("Number", tm.networkOperatorName) // this must be MTN Rwanda or the USSD won't run

        mPhoneNumber = if (tm.networkOperatorName != "MTN Rwanda" || mPhoneNumber == "")
            ""
        else
            mPhoneNumber


        val sharedPref = getSharedPreferences("number_amount", Context.MODE_PRIVATE)
        val myNumber = sharedPref.getString("MY_NUMBER", mPhoneNumber)

        dataEdt.setText(myNumber)
        generateQr (dataEdt.text.toString())



        // mScannerView = ZXingScannerView(this)   // Programmatically initialize the scanner view
        // setContentView(mScannerView)
    }

    fun generateQr (num : String)
    {
        val qrCodeIV = findViewById<ImageView>(R.id.idIVQrcode)

        val mPhoneNumber = if (num == "")
            "*182*1*1#"
        else
            "*182*1*1*${num.replace("+250", "0").replace(" ", "")}#"

        // below line is for getting
        // the windowmanager service.
        val manager = getSystemService(WINDOW_SERVICE) as WindowManager

        // initializing a variable for default display.
        val display = manager.defaultDisplay

        // creating a variable for point which
        // is to be displayed in QR Code.
        val point = Point()
        display.getSize(point)

        // getting width and
        // height of a point
        val width: Int = point.x
        val height: Int = point.y

        // generating dimension from width and height.
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder =
                QRGEncoder(mPhoneNumber, null, QRGContents.Type.TEXT, dimen)
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder!!.encodeAsBitmap()
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            qrCodeIV.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> finish()
        }
        return true
    }

    var numberValue = ""

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Log.i("textChanged", s.toString())
            val newValue = s.toString().replace(" ", "")

            if (newValue != numberValue)
            {
                numberValue = newValue
                var ret = ""

                if ((newValue.startsWith("07") && newValue.length <= 10) || newValue == "0") {
                    for (i in newValue.indices) {
                        if (i == 4 || i == 7)
                            ret += " "
                        ret += newValue[i]
                    }
                }

                dataEdt.setText(ret.trim())
                dataEdt.setSelection(dataEdt.length() - 0)

                generateQr (dataEdt.text.toString())


                val sharedPref = getSharedPreferences("number_amount", Context.MODE_PRIVATE)
                with (sharedPref?.edit()) {
                    this?.putString("MY_NUMBER", dataEdt.text.toString())
                    this?.apply()
                }
            }
        }
    }


    private var mScannerView: ZXingScannerView? = null

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
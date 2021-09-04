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
import com.google.zxing.WriterException
import com.rmsoft.moneza.R

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    lateinit var dataEdt : EditText
    var bitmap: Bitmap? = null
    var qrgEncoder: QRGEncoder? = null

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
        val root = inflater.inflate(R.layout.fragment_qr, container, false)



        // initializing all variables.
        dataEdt = root.findViewById<EditText>(R.id.idEdt)
        // val generateQrBtn = findViewById<Button>(R.id.idBtnGenerateQR)

        dataEdt.addTextChangedListener(textWatcher)





        // Get a support ActionBar corresponding to this toolbar and enable the Up button

        val tm = requireActivity().applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        var mPhoneNumber = if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ""
        } else
            tm.line1Number

        Log.i("Number", tm.networkOperatorName) // this must be MTN Rwanda or the USSD won't run

        mPhoneNumber = if (tm.networkOperatorName != "MTN Rwanda" || mPhoneNumber == "")
            ""
        else
            mPhoneNumber


        val sharedPref = requireActivity().getSharedPreferences("number_amount", Context.MODE_PRIVATE)
        val myNumber = sharedPref.getString("MY_NUMBER", mPhoneNumber)

        dataEdt.setText(myNumber)
        generateQr (dataEdt.text.toString())

        return root
    }

    fun generateQr (num : String)
    {
        val qrCodeIV = view?.findViewById<ImageView>(R.id.idIVQrcode)

        val mPhoneNumber = if (num == "")
            "*182*1*1#"
        else
            "*182*1*1*${num.replace("+250", "0").replace(" ", "")}#"

        // below line is for getting
        // the windowmanager service.
        val manager = requireActivity().getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager

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
            qrCodeIV?.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString())
        }
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


                val sharedPref = requireActivity().getSharedPreferences("number_amount", Context.MODE_PRIVATE)
                with (sharedPref?.edit()) {
                    this?.putString("MY_NUMBER", dataEdt.text.toString())
                    this?.apply()
                }
            }
        }
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
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
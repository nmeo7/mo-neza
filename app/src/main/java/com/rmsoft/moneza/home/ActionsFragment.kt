package com.rmsoft.moneza.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.flatdialoglibrary.dialog.FlatDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rmsoft.moneza.R
import com.rmsoft.moneza.actions.ActionsActivity
import com.rmsoft.moneza.util.CheckPrivileges
import com.rmsoft.moneza.util.DataPersistence


/**
 * A simple [Fragment] subclass.
 */
class ActionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val dataPersistence = DataPersistence(requireActivity())
        dataPersistence.find()

        return inflater.inflate(R.layout.fragment_actions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(context, ActionsActivity::class.java)

        number = view.findViewById(R.id.number) as TextInputEditText
        amount = view.findViewById(R.id.amount) as TextInputEditText
        amountLayout = view.findViewById(R.id.amount_layout) as TextInputLayout

        number.addTextChangedListener(textWatcher)
        amount.addTextChangedListener(textWatcherMoney)

        amountLayout.suffixText = "Fee"

        numberLayout = view.findViewById(R.id.number_layout) as TextInputLayout

        number.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                validateEditText((v as EditText).text)
            }
        }

        number.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= number.right - number.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    // your action here
                    Log.i("clicked", "clicked")
                    v.performClick()
                    true
                }
                if (event.rawX <= number.left + number.compoundDrawables[DRAWABLE_LEFT].bounds.width() + 64) {
                    val flatDialog = FlatDialog(requireContext())
                    flatDialog.setTitle("Login")
                        .setSubtitle("write your profile info here")
                        .setFirstTextFieldHint("email")
                        .setSecondTextFieldHint("password")
                        .setFirstButtonText("CONNECT")
                        .setSecondButtonText("CANCEL")
                        .withFirstButtonListner {
                            // do something ...
                        }
                        .withSecondButtonListner {
                            flatDialog.dismiss()
                        }
                        .show()

                    Log.i("clicked", "clicked")
                    v.performClick()
                    true
                }
            }
            v.performClick()
            false
        })

        view.findViewById<Button>(R.id.action_balance).setOnClickListener {
            if (CheckPrivileges(requireContext(), requireActivity()).requestCallPhonePermission ())
                return@setOnClickListener

            numberValue = number.text.toString().replace(" ", "")
            amountValue = amount.text.toString().replace(" ", "")


            if (numberValue.startsWith("07") && numberValue.length == 10)
            {
                var ussdCode = "*182*1*1*$numberValue*$amountValue#"
                Log.i("ussdCode", ussdCode)

                ussdCode = ussdCode.substring(0, ussdCode.length - 1)
                val ussdCodeNew = ussdCode + Uri.encode("#")
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))
            }

            else if (numberValue.length == 6 || numberValue.length == 5)
            {
                var ussdCode = "*182*8*1*$numberValue*$amountValue#"

                ussdCode = ussdCode.substring(0, ussdCode.length - 1)
                val ussdCodeNew = ussdCode + Uri.encode("#")
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))
            }
            else if (numberValue.length == 11)
            {
                // 01311104283
                var ussdCode = "*182*2*2*1*2*$numberValue*$amountValue#"
                Log.i("ussdCode", ussdCode)

                ussdCode = ussdCode.substring(0, ussdCode.length - 1)
                val ussdCodeNew = ussdCode + Uri.encode("#")
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))
            }
            else if (numberValue.length == 12)
            {
                // 01311104283
                var ussdCode = "*182*2*2*1*2*$numberValue*$amountValue#"
                Log.i("ussdCode", ussdCode)

                ussdCode = ussdCode.substring(0, ussdCode.length - 1)
                val ussdCodeNew = ussdCode + Uri.encode("#")
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))
            }
        }
    }


    private fun validateEditText(s: Editable?) {
        if (!TextUtils.isEmpty(s)) {
            amountLayout.error = null
        } else {
            amountLayout.error = "Goddamn it"
        }
    }

    lateinit var number: TextInputEditText
    var numberValue = ""

    lateinit var amount: TextInputEditText
    var amountValue = ""
    private lateinit var amountLayout: TextInputLayout

    lateinit var numberLayout: TextInputLayout

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Log.i("textChanged", s.toString())
            val newValue = s.toString().replace(" ", "")
            val button = view?.findViewById<Button>(R.id.action_balance)
            if (newValue != numberValue)
            {
                numberValue = newValue
                var ret = ""

                numberLayout.hint = "kode, nimero, etc"
                button?.isEnabled = true

                if ((newValue.startsWith("07") && newValue.length <= 10) || newValue == "0") {
                    for (i in newValue.indices) {
                        if (i == 4 || i == 6 || i == 8)
                            ret += " "
                        ret += newValue[i]
                    }
                    button?.text = "Kohereza"
                    numberLayout.hint = "nimero"
                }
                else if (newValue.length in 1..6) {
                    for (i in newValue.indices) {
                        if (i % 2 == 0)
                            ret += " "
                        ret += newValue[i]
                    }
                    button?.text = "Kwishyura"
                    numberLayout.hint = "kode"
                }
                else if (newValue.length in 1..12){
                    for (i in newValue.indices) {
                        if (i == 2 || i == 6 || i == 10)
                            ret += " "
                        ret += newValue[i]
                    }
                    button?.text = "Kwishyura"
                    if (newValue.length == 12)
                        numberLayout.hint = "kode y'irembo"
                    else
                        numberLayout.hint = "kashi pawa / irembo"
                }
                else if (newValue != "")
                {
                    button?.text = "???"
                    button?.isEnabled = false
                    numberLayout.hint = "???"
                    ret = newValue
                }

                number.setText(ret.trim())
                number.setSelection(number.length()-0)
            }
        }
    }

    private val textWatcherMoney = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            validateEditText(s)
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val newValue = s.toString().replace(" ", "")
            if (newValue != amountValue)
            {
                amountValue = newValue
                var ret = ""

                for (i in newValue.length-1 downTo  0) {
                    ret = if ( (newValue.length - i) % 3 == 0)
                        " " + newValue[i] + ret
                    else
                        "" + newValue[i] + ret
                }

                amount.setText(ret.trim())
                amount.setSelection(amount.length()-0)
            }
        }
    }


}

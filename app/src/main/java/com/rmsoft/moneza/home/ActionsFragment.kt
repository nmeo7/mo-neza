package com.rmsoft.moneza.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.TelephonyManager
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rmsoft.moneza.R
import com.rmsoft.moneza.StateMachine
import com.rmsoft.moneza.actions.ActionsActivity
import com.rmsoft.moneza.actions.CompleteActionActivity
import com.rmsoft.moneza.util.CheckPrivileges
import com.rmsoft.moneza.util.DataPersistence


/**
 * A simple [Fragment] subclass.
 */
class ActionsFragment : Fragment() {

    private val viewModel: StateMachine by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val dataPersistence = DataPersistence(requireActivity())
        dataPersistence.find()

        return inflater.inflate(R.layout.fragment_actions, container, false)
    }

    private fun setButtonText ()
    {

    }

    val resultLauncher2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            amount.setText("")
        }
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

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val contactUri = result.data!!.data!!

                val cursor = context?.contentResolver?.query(contactUri, null, null, null, null);

                Log.i("PEOPLE", contactUri.toString())

                // If the cursor returned is valid, get the phone number
                if (cursor != null && cursor.moveToFirst()) {
                    val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    var num = cursor.getString(numberIndex)
                    Log.i("PEOPLE", num)
                    if (num.startsWith("+25"))
                        num = num.replace("+25", "")
                    number.setText(num)
                    Log.i("PEOPLE", num)
                }

                cursor?.close();
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

                    if (!CheckPrivileges(
                                    requireContext(),
                                    requireActivity()
                            ).requestReadContactPermission()
                    ) {
                        val i = Intent(Intent.ACTION_PICK)
                        i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                        resultLauncher.launch(i)
                    }

                    v.performClick()
                    true
                }
                /*
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
                */
            }
            v.performClick()
            false
        })

        view.findViewById<Button>(R.id.action_balance).setOnClickListener {
            if (CheckPrivileges(requireContext(), requireActivity()).requestCallPhonePermission ())
                return@setOnClickListener


            val tm = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            /*
            if (tm.networkOperatorName != "MTN Rwanda")
            {
                Toast.makeText(requireActivity(), "Iki gikorwa kibasha gukorwa kuri simu kadi ya MTN gusa!", Toast.LENGTH_LONG).show();
                return@setOnClickListener
            }*/

            numberValue = number.text.toString().replace(" ", "")
            amountValue = amount.text.toString().replace(" ", "")

            if (numberValue == "" && amountValue == "")
            {
                var ussdCode = "*182*6*1#"
                Log.i("ussdCode", ussdCode)

                ussdCode = ussdCode.substring(0, ussdCode.length - 1)
                val ussdCodeNew = ussdCode + Uri.encode("#")
                // startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))
            }
            else if (numberValue == "" && amountValue.isNotEmpty())
            {
                var ussdCode = "*182*2*1*1*1*$amountValue#"
                Log.i("ussdCode", ussdCode)

                ussdCode = ussdCode.substring(0, ussdCode.length - 1)
                val ussdCodeNew = ussdCode + Uri.encode("#")
                // startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))
            }
            else if (numberValue.startsWith("07") && numberValue.length == 10)
            {
                var ussdCode = "*182*1*1*$numberValue*$amountValue#"
                Log.i("ussdCode", ussdCode)

                ussdCode = ussdCode.substring(0, ussdCode.length - 1)
                val ussdCodeNew = ussdCode + Uri.encode("#")
                // startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))
            }

            else if (numberValue.length == 6 || numberValue.length == 5)
            {
                var ussdCode = "*182*8*1*$numberValue*$amountValue#"

                ussdCode = ussdCode.substring(0, ussdCode.length - 1)
                val ussdCodeNew = ussdCode + Uri.encode("#")
                // startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))
            }
            else if (numberValue.length == 11)
            {
                // 01311104283
                var ussdCode = "*182*2*2*1*2*$numberValue*$amountValue#"
                Log.i("ussdCode", ussdCode)

                ussdCode = ussdCode.substring(0, ussdCode.length - 1)
                val ussdCodeNew = ussdCode + Uri.encode("#")
                // startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))
            }
            else if (numberValue.length == 12)
            {
                // 01311104283
                var ussdCode = "*182*2*2*1*2*$numberValue*$amountValue#"
                Log.i("ussdCode", ussdCode)

                ussdCode = ussdCode.substring(0, ussdCode.length - 1)
                val ussdCodeNew = ussdCode + Uri.encode("#")
                // startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))
            }

            // val item = Message()
            // item.subjectNumber = numberValue
            // item.amount = amountValue.toInt()

            // viewModel.selectMessage(item)

            val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)

            var msg = ""

            for (x in chipGroup.checkedChipIds)
            {
                msg += "" + view.findViewById<Chip>(x).text + "; "
            }

            Log.i("CHIPS", msg)


            val sharedPref = activity?.getSharedPreferences("number_amount", Context.MODE_PRIVATE)
            with(sharedPref?.edit()) {
                this?.putString("NUMBER", numberValue)
                this?.putString("AMOUNT", amountValue)
                this?.putString("MESSAGE", msg)
                this?.putLong("TIME", System.currentTimeMillis())
                this?.apply()
            }

            val myIntent = Intent(requireActivity(), CompleteActionActivity::class.java)
            myIntent.putExtra("number", numberValue) //Optional parameters
            myIntent.putExtra("amount", amountValue) //Optional parameters
            myIntent.putExtra("msg", msg) //Optional parameters

            // requireActivity().startActivityForResult(myIntent, 1234)

            resultLauncher2.launch(myIntent)
        }

        viewModel.selectedMessage.observe(viewLifecycleOwner, Observer { message ->
            amount.setText(message.amount.toString())
            number.setText(message.subjectNumber)
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("RESULT", resultCode.toString())
        if (resultCode == Activity.RESULT_OK) {
            amount.setText("")
        }
    } //onActivityResult


    private fun validateEditText(s: Editable?) {
        if (!TextUtils.isEmpty(s)) {
            amountLayout.error = null
        } else {
            amountLayout.error = resources.getString(R.string.amount_error)
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

                numberLayout.hint = resources.getString(R.string.number_text_hint)
                button?.isEnabled = true

                if ((newValue.startsWith("07") && newValue.length <= 10) || newValue == "0") {
                    for (i in newValue.indices) {
                        if (i == 4 || i == 7)
                            ret += " "
                        ret += newValue[i]
                    }
                    // button?.text = resources.getString(R.string.send)
                    numberLayout.hint = resources.getString(R.string.hint_number)
                }
                else if (newValue.length in 1..6) {
                    for (i in newValue.indices) {
                        if (i % 2 == 0)
                            ret += " "
                        ret += newValue[i]
                    }
                    // button?.text = resources.getString(R.string.pay)
                    numberLayout.hint = resources.getString(R.string.hint_code)
                }
                else if (newValue.length in 1..12){
                    for (i in newValue.indices) {
                        if (i == 2 || i == 6 || i == 10)
                            ret += " "
                        ret += newValue[i]
                    }
                    // button?.text = resources.getString(R.string.pay)
                    if (newValue.length == 12)
                        numberLayout.hint = resources.getString(R.string.hint_irembo)
                    else
                        numberLayout.hint = resources.getString(R.string.hint_cash_power)
                }
                else if (newValue != "")
                {
                    // button?.text = "???"
                    button?.isEnabled = false
                    numberLayout.hint = "???"
                    ret = newValue
                }

                if (newValue == "" && numberValue == "") {
                    // button?.isEnabled = false
                    // button?.text = resources.getString(R.string.balance)
                }

                number.setText(ret.trim())
                number.setSelection(number.length() - 0)
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
                amount.setSelection(amount.length() - 0)
            }

            val button = view?.findViewById<Button>(R.id.action_balance)
            if (newValue.isNotEmpty() && numberValue == "") {
                // button?.text = resources.getString(R.string.airtime)
            }

            if (newValue == "" && numberValue == "") {
                // button?.text = resources.getString(R.string.balance)
            }
        }
    }


}

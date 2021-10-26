package com.rmsoft.moneza.actions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.rmsoft.moneza.R
import com.rmsoft.moneza.util.CheckPrivileges
import com.rmsoft.moneza.util.Message
import java.util.*


class CompleteActionActivity : AppCompatActivity() {
    var doChallenge = false
    var challengeNumber = ""
    var challenge = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_action)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.toolbar_bg))

        val numberValue = intent.getStringExtra("number")!! //if it's a string you stored.
        val amountValue = intent.getStringExtra("amount")!! //if it's a string you stored.
        val msg = intent.getStringExtra("msg") //if it's a string you stored.

        val img = findViewById<ImageView>(R.id.confirm_img)
        img.setImageResource(R.drawable.ic_code)

        var ussdCode = if (numberValue == "" && amountValue == "")
            "*182*6*1#"
        else if (numberValue == "" && amountValue.isNotEmpty())
            "*182*2*1*1*1*$amountValue#"
        else if (numberValue.startsWith("07") && numberValue.length == 10)
            "*182*1*1*$numberValue*$amountValue#"
        else if (numberValue.length == 6 || numberValue.length == 5)
            "*182*8*1*$numberValue*$amountValue#"
        else if (numberValue.length == 11)
            "*182*2*2*1*2*$numberValue*$amountValue#"
        else if (numberValue.length == 12)
            "*182*2*2*1*2*$numberValue*$amountValue#"
        else
            "182#"

        val cal = Calendar.getInstance()
        // cal.time = now()
        val hours = cal.get(Calendar.HOUR_OF_DAY)

        Log.i("HOUR", hours.toString())

        val amount = if (amountValue == "") 0 else amountValue.toInt()

        if (amount > 9999)
            doChallenge = true
        if (amount > 1999 && hours > 21 && hours < 8)
            doChallenge = true

        if (numberValue.isNotEmpty())
        {
            val i = Random().nextInt(numberValue.length)
            challengeNumber = numberValue[i].toString()

            Log.i("THE CHALLLENGE", challengeNumber)

            for (x in numberValue.indices)
                challenge = challenge.plus(if (x == i) '*' else numberValue[x])
        }
        else {
            challengeNumber = "Airtime"
            val i = Random().nextInt(challengeNumber.length)

            Log.i("THE CHALLLENGE", challengeNumber)

            for (x in challengeNumber.indices)
                challenge = challenge.plus(if (x == i) '*' else challengeNumber[x])

            challengeNumber = challengeNumber[i].toString()
        }

        val label = findViewById<TextView>(R.id.confirm_challenge_text)
        label.text = challenge

        if (doChallenge)
        {
            findViewById<EditText>(R.id.confirm_challenge).visibility = View.VISIBLE
            label.visibility = View.VISIBLE
            findViewById<TextView>(R.id.confirm_challenge_label).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.challenge).visibility = View.VISIBLE

        }
        else
        {
            findViewById<EditText>(R.id.confirm_challenge).visibility = View.GONE
            label.visibility = View.GONE
            findViewById<TextView>(R.id.confirm_challenge_label).visibility = View.GONE
            findViewById<LinearLayout>(R.id.challenge).visibility = View.GONE
        }


        if (numberValue.startsWith("07") && numberValue.length == 10)
            img.setImageResource(R.drawable.ic_woman)
        else if (numberValue.length == 6 || numberValue.length == 5 || numberValue.length == 11 || numberValue.length == 12)
            img.setImageResource(R.drawable.ic_store)
        else if (numberValue == "" && amountValue.isNotEmpty())
            img.setImageResource(R.drawable.ic_telephone)


        var numberValueString = ""
        if ((numberValue.startsWith("07") && numberValue.length <= 10) || numberValue == "0") {
            for (i in numberValue.indices) {
                if (i == 4 || i == 7)
                    numberValueString += " "
                numberValueString += numberValue[i]
            }
        }
        else if (numberValue.length in 1..6) {
            for (i in numberValue.indices) {
                if (i % 2 == 0)
                    numberValueString += " "
                numberValueString += numberValue[i]
            }
        }
        else if (numberValue.length in 1..12){
            for (i in numberValue.indices) {
                if (i == 2 || i == 6 || i == 10)
                    numberValueString += " "
                numberValueString += numberValue[i]
            }
        }
        else if (numberValue.isEmpty() && amountValue.isEmpty())
            numberValueString = resources.getString(R.string.balance)
        else if (numberValue.isEmpty())
            numberValueString = "Airtime"
        else
            numberValueString = "???"

        findViewById<TextView>(R.id.confirm_number).text = numberValueString
        findViewById<TextView>(R.id.confirm_amount).text = "-"
        if (amountValue.isNotEmpty())
            findViewById<TextView>(R.id.confirm_amount).text = Message().parseAmount(amountValue.toInt())
        findViewById<TextView>(R.id.confirm_code).text = ussdCode
        findViewById<TextView>(R.id.confirm_purpose).text = msg

        findViewById<TextView>(R.id.confirm_code).setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", ussdCode)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(baseContext, "code copied!", Toast.LENGTH_SHORT).show()
        }

        val activity = this

        findViewById<Button>(R.id.action_confirm).setOnClickListener {
            if (CheckPrivileges(this, this).requestCallPhonePermission ())
                return@setOnClickListener

            Log.i("CHALLENGE", "taking challenge...")
            Log.i("CHALLENGE ANSWER", challengeNumber)

            if (doChallenge && findViewById<TextInputEditText>(R.id.confirm_challenge).text.toString() != challengeNumber)
            {
                Log.i("CHALLENGE", "challenge not passed")
                // tell them that they didn't complete the challenge
                Toast.makeText(baseContext, "please complete the challenge!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.i("CHALLENGE", "challenge passed")

            ussdCode = ussdCode.substring(0, ussdCode.length - 1)
            val ussdCodeNew = ussdCode + Uri.encode("#")
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussdCodeNew")))

            val sharedPref = getSharedPreferences("number_amount", Context.MODE_PRIVATE)
            with(sharedPref?.edit()) {
                this?.putString("NUMBER", numberValue)
                this?.putString("AMOUNT", amountValue)
                this?.putString("MESSAGE", msg)
                this?.putLong("TIME", System.currentTimeMillis())
                this?.apply()
            }

            val resultIntent = Intent()
            setResult(RESULT_OK, resultIntent)
            finish()


        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val resultIntent = Intent()
                setResult(RESULT_CANCELED, resultIntent)
                finish()
            }
        }
        return true
    }
}
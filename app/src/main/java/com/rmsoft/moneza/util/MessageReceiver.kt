package com.rmsoft.moneza.util

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import com.rmsoft.moneza.MainActivity

class MessageReceiver() : BroadcastReceiver() {

    private val TAG: String = "Sms Receiver"
    val pdu_type = "pdus"

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    override fun onReceive(context: Context, intent: Intent) {

        // val persistence = DataPersistence(this)

        // Get the SMS message.
        val bundle = intent.extras
        val msgs: Array<SmsMessage?>
        var strMessage = ""
        val format = bundle!!.getString("format")


        Log.d(TAG, "onReceive: message message!!")

        // Retrieve the SMS message received.
        val pdus = bundle[pdu_type] as Array<Any>?
        if (pdus != null) {
            // Check the Android version.
            val isVersionM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            // Fill the msgs array.
            msgs = arrayOfNulls<SmsMessage>(pdus.size)
            for (i in msgs.indices) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                } else {
                    // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                }
                // Build the message to show.
                strMessage += "SMS from " + msgs[i]?.originatingAddress
                strMessage += """ :${msgs[i]?.messageBody.toString()}"""
                // Log and display the SMS message.

                // maContext.notifySmsReceived(strMessage)

                if (msgs[i]?.originatingAddress == "")
                {
                    val m = ParseMessage().parseMessage (msgs[i]?.messageBody.toString())
                    DataPersistence(context)
                    // maybe here find a way to save data locally before persisting it to the realm database
                }
            }
        }
    }
}
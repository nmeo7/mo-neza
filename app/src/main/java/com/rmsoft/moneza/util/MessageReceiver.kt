package com.rmsoft.moneza.util

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Message
import android.provider.ContactsContract
import android.telephony.SmsMessage
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rmsoft.moneza.MainActivity
import com.rmsoft.moneza.R
import com.rmsoft.moneza.StateMachine


class MessageReceiver : BroadcastReceiver() {

    companion object MessageReceiver

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

            var msgBody = ""
            var sender = ""
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
                if (msgs[i]?.originatingAddress != null)
                    sender = msgs[i]?.originatingAddress!!
                msgBody += msgs[i]?.messageBody.toString()
                // Log and display the SMS message.
            }

            Log.i("OnMessage", "$sender: $msgBody")

            if (sender == "M-Money")
            {
                val m = ParseMessage().parseMessage(msgBody)
                // maybe here find a way to save data locally before persisting it to the realm database
                var saved = false

                if (m.type == "PAYMENT")
                {
                    val sharedPref = context.getSharedPreferences("number_amount", Context.MODE_PRIVATE)
                    val amount = sharedPref.getString("AMOUNT", "0")
                    val number = sharedPref.getString("NUMBER", "")
                    val time = sharedPref.getLong("TIME", Long.MAX_VALUE)
                    val message = sharedPref.getString("MESSAGE", "")

                    // reset the shared prefs
                    with (sharedPref?.edit()) {
                        this?.putString("NUMBER", "")
                        this?.putString("AMOUNT", "")
                        this?.putString("MESSAGE", "")
                        this?.putLong("TIME", 0)
                        this?.apply()
                    }

                    if (m.amount == amount?.toInt() && (System.currentTimeMillis() < time + 1000 * 120))
                    {
                        m.subjectNumber = number
                        m.message = message
                        DataPersistence(context).save(m)
                        Log.i("Connect", m.toString())
                        saved = true
                    }
                }
                    // maContext?.notifySmsReceived(m)

                if (!saved)
                    DataPersistence(context).save(m)

                // showNotification(context)
            }
        }
    }

    fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "all_notifications" // You should create a String resource for this instead of storing in a variable
            val mChannel = NotificationChannel(
                    channelId,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            mChannel.description = "This is default channel used for all other notifications"

            val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun showNotification(context: Context) {
        createNotificationChannel(context)
        val channelId = "all_notifications" // Use same Channel ID
        val intent = Intent(context, ContactsContract.Profile::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        val builder = NotificationCompat.Builder(context, channelId) // Create notification with channel Id
                .setSmallIcon(R.drawable.ic_dashboard)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
        builder.setContentIntent(pendingIntent).setAutoCancel(true)
        // val mNotificationManager = Message.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // with(mNotificationManager) {
            // notify(123, builder.build())
        // }

        with(NotificationManagerCompat.from(context)) {
            notify(123, builder.build())
        }

    }
}
package com.rmsoft.moneza.util

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.rmsoft.moneza.R
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class MessageReadAll (activity: Activity) {
    val FAKE_MESSAGES = true
    val persistence = DataPersistence(activity)

    private fun saveMessage (data : Message)
    {
        persistence.save(data)
    }

    private fun readMessagesFake (context: Context)
    {
        Log.d("READ_MOMO", "STARTING...")

        val inputStream: InputStream = context.resources.openRawResource(R.raw.momo)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        try {
            var eachline = bufferedReader.readLine()
            while (eachline != null) {
                val m = ParseMessage().parseMessage(eachline)
                persistence.save(m)
                eachline = bufferedReader.readLine()
            }
        } catch (e: Exception) {
            Log.e("", "ERROR!")
        }

        Log.d("READ_MOMO", "DONE.")
    }

    private fun readMessagesReal (context: Context)
    {

        Log.d("READ_SMS", "Starting")


        val cursor: Cursor? = context.contentResolver.query(
                Uri.parse("content://sms/inbox"), null, null, null, null
        )

        if (cursor?.moveToFirst()!!) { // must check the result to prevent exception
            do {
                var msgData = ""
                if (cursor.getString(2) != "M-Money")
                    continue

                for (idx in 0 until cursor.columnCount) {
                    if (cursor.getColumnName(idx).toString() == "body")
                        msgData += cursor.getString( idx )
                }
                msgData = msgData.replace('\n', ' ').trim()
                // Log.d("READ_SMS", msgData.replace('\n', ' ').trim())

                val m = ParseMessage().parseMessage(msgData)
                persistence.save(m)

                // use msgData
            } while (cursor.moveToNext())
        } else {
            // empty box, no SMS
            Log.d("READ_SMS", "Emptiness")
        }

        cursor.close()
    }

    fun readMessages (context: Context)
    {
        if (FAKE_MESSAGES)
            readMessagesFake(context)
        else
            readMessagesReal(context)
    }
}
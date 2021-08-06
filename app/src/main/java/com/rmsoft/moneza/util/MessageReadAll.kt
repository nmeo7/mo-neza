package com.rmsoft.moneza.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log
import com.rmsoft.moneza.R
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList

class MessageReadAll (private val activity: Activity, private var fromSms : Boolean = true) {
    private val persistence = DataPersistence(activity)

    private fun saveMessage (data : Message)
    {
        persistence.save(data)
    }

    private fun readMessagesFromFile (context: Context) : ArrayList<Message>
    {
        Log.d("READ_MOMO", "STARTING ...")

        val inputStream: InputStream = context.resources.openRawResource(R.raw.momo)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))


        val contacts = ArrayList<Message>()

        try {
            var eachline = bufferedReader.readLine()
            while (eachline != null) {
                val m = ParseMessage().parseMessage(eachline)
                contacts.add(m)

                Log.i("MOMO_READ", m.toString())
                persistence.save(m)
                eachline = bufferedReader.readLine()
            }
        } catch (e: Exception) {
            Log.e("MOMO_READ", e.toString())
        }

        Log.d("READ_MOMO", "DONE.")

        return contacts
    }

    private fun readMessagesFromSmss (context: Context) : ArrayList<Message>
    {
        val contacts = ArrayList<Message>()

        if (  CheckPrivileges(context,activity).runtimeAskPrivileges (Manifest.permission.READ_SMS) )
            return contacts

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

                contacts.add(m)
                // use msgData
            } while (cursor.moveToNext())
        } else {
            // empty box, no SMS
            Log.d("READ_SMS", "Emptiness")
        }

        cursor.close()
        return contacts
    }

    fun readDatabase ()
    {
        persistence.find()
    }

    // Request code for creating a PDF document.
    val CREATE_FILE = 1

    fun createFile(pickerInitialUri: Uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, "example.txt")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
                }
            }

        activity.startActivityForResult(intent, CREATE_FILE)
    }

    val PICK_PDF_FILE = 2

    fun openFile(pickerInitialUri: Uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
            }
        }

        activity.startActivityForResult(intent, PICK_PDF_FILE)

    }



    fun readMessages (context: Context) : ArrayList<Message>
    {
        if (fromSms)
            return readMessagesFromSmss(context)
        else
            return readMessagesFromFile(context)
    }
}
package com.rmsoft.moneza.home.dashboard


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rmsoft.moneza.R
import com.rmsoft.moneza.util.MessageReadAll
import java.io.*


class DashboardFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    private fun parseMoney(it: List<Int>) : String
    {
        val ret = try {
            it.first()
        } catch (e: Exception) {
            0
        }

        if (ret >= 1000000)
        {
            val m = (ret / 1000000).toString()
            var k = ((ret / 1000) % 1000).toString()
            if (k.length == 1) k = "00$k"
            if (k.length == 2) k = "0$k"
            var u = (ret % 1000).toString()
            if (u.length == 1) u = "00$u"
            if (u.length == 2) u = "0$u"

            return "$m $k $u"
        }

        if (ret >= 1000)
        {
            val k = (ret / 1000).toString()
            var u = (ret % 1000).toString()
            if (u.length == 1) u = "00$u"
            if (u.length == 2) u = "0$u"

            return "$k $u"
        }

        return ret.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val commission = view.findViewById<TextView>(R.id.dash_commission)
        val balance = view.findViewById<TextView>(R.id.dash_balance)
        val deposit = view.findViewById<TextView>(R.id.dash_deposit)
        val payments = view.findViewById<TextView>(R.id.dash_payments)
        val received = view.findViewById<TextView>(R.id.dash_received)
        val sent = view.findViewById<TextView>(R.id.dash_sent)
        val total = view.findViewById<TextView>(R.id.dash_total)
        val withdrawals = view.findViewById<TextView>(R.id.dash_withdraws)

        val savings = view.findViewById<TextView>(R.id.dash_savings)
        val spendings = view.findViewById<TextView>(R.id.dash_spendings)
        val starting = view.findViewById<TextView>(R.id.dash_starting)
        val closing = view.findViewById<TextView>(R.id.dash_closing)
        val fee = view.findViewById<TextView>(R.id.dash_fee)

        // MessageReadAll(requireActivity()).createFile(Uri.EMPTY)
        // MessageReadAll(requireActivity()).openFile (Uri.EMPTY)


        /*
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "example.txt")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.EMPTY)
            }
        }

        startActivityForResult(intent, 4) */




        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "text/plain"
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        startActivityForResult(chooseFile, 3)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            3 -> if (resultCode == RESULT_OK) {
                val fileUri = data?.data
                val filePath = fileUri?.path
                Log.i("filePath", filePath.toString())
                Log.i("filePath", fileUri.toString())

                val contentResolver = requireContext().contentResolver
                val stringBuilder = StringBuilder()
                contentResolver.openInputStream(fileUri!!)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line)
                            line = reader.readLine()
                        }
                    }
                }
                Log.i("filePath_content", "content $stringBuilder")
            }

            4 -> if (resultCode == RESULT_OK) {
                val outputStream: OutputStream
                val fileUri = data?.data
                try {
                    outputStream = requireActivity().contentResolver.openOutputStream(fileUri!!)!!
                    val bw = BufferedWriter(OutputStreamWriter(outputStream))
                    bw.write("bison is bision")
                    bw.flush()
                    bw.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}

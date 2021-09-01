package com.rmsoft.moneza.util

import android.os.Bundle
import android.util.Log
import com.rmsoft.moneza.R
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class ParseMessage {
    internal enum class Type {
        PAYMENT, TRANSFER, RECEIVING, WITHDRAWAL, DEPOSIT, OTHER
    }

    private fun toDict(st: String): Message {
        val ss = st.split("\n").toTypedArray()
        val ret = Message()
        for (s in ss) {
            val key = s.split(" ")[0]
            val value = s.split(" ", ignoreCase = false, limit = 2).last()
            ret.setContent(key, value)
        }

        ret.makeHash(st)
        return ret
    }

    private fun parseWithdrawal(msg: String): String {
        var st = msg
        st = st.replace("*153*TxId:", "\nTRANSACTION_ID ")
        st = st.replace(" has via agent: ", "\nAGENT ")
        st = st.replace(", withdrawn ", "\nAMOUNT ")
        st = st.replace(" RWF from your mobile money account: ", "\nDEBIT_ACCOUNT ")
        st = st.replace(" at 20", "\nTIME 20")
        st = st.replace(
            " and you can now collect your money in cash. Your new balance: ",
            "\nBALANCE "
        )
        st = st.replace(" RWF. Fee paid: ", "\nFEE ")
        st = st.replace(" RWF. Message from agent: ", "\nMESSAGE ")
        st = st.replace(". *EN#", "\nEXTRA ")
        st = st.replace("*S* ", "\n")
        return "TYPE WITHDRAW$st"
    }

    private fun parseTransfer(msg: String): String {
        var st = msg
        st = st.replace("*165*S*", "\nTRANSACTION_ID ")
        st = st.replace(" RWF transferred to ", "\nRECIPIENT ")
        st = st.replace(" from ", "\nSENDER ")
        st = st.replace(" at 20", "\nTIME 20")
        st = st.replace(" . Fee was: ", "\nFEE ")
        st = st.replace(" RWF. New balance: ", "\nBALANCE ")
        st = st.replace(" . New balance: ", "\nBALANCE ")
        st = st.replace(" RWF. ", "\nEXTRA ")
        return "TYPE TRANSFER$st"
    }

    private fun parseReceiving(msg: String): String {
        var st = msg
        if (st.startsWith("*165*R")) {
            st = st.replace("*165*R*You have received ", "\nAMOUNT ")
            st = st.replace(" RWF from ", "\nSENDER ")
            st = st.replace(" on your mobile money account at ", "\nTIME ")
            st = st.replace(" . Your new balance:", "\nBALANCE ")
            st = st.replace(" RWF.", "\nEXTRA ")
        }
        if (st.startsWith("*134*R")) {
            st = st.replace("*134*R*You have received ", "\nAMOUNT ")
            st = st.replace(" RWF from ", "\nSENDER ")
            st = st.replace(" on your mobile money account at ", "\nTIME ")
            st = st.replace(". Message from sender: ", "\nMESSAGE ")
            st = st.replace(".Your NEW BALANCE: ", "\nBALANCE ")
            st = st.replace(" RWF.", "\nEXTRA ")
        }
        return "TYPE RECEIVING$st"
    }

    private fun parseDeposit(msg: String): String {
        var st = msg
        st = st.replace("*135*R*You have received ", "\nAMOUNT ")
        st = st.replace(" RWF from ", "\nSENDER ")
        st = st.replace(" on your mobile money account at ", "\nTIME ")
        st = st.replace(". Message from sender: ", "\nMESSAGE ")
        st = st.replace(".Your new balance:", "\nBALANCE ")
        st = st.replace(" RWF.", "\nEXTRA ")
        return "TYPE DEPOSIT$st"
    }

    private fun parsePayment(msg: String): String {
        var st = msg
        if (st.startsWith("*162*")) {
            st = st.replace("*162*TxId:", "\nTRANSACTION_ID ")
            st = st.replace("*S*Your payment of ", "\nAMOUNT ")
            st = st.replace(" RWF to ", "\nSERVICE ")
            st = st.replace(" with token ", "\nTOKEN ")
            st = st.replace(" has been completed at ", "\nTIME ")
            st = st.replace(". Your new balance: ", "\nBALANCE ")
            st = st.replace(" RWF . Message: ", "\nMESSAGE ")
            st = st.replace(". *EN#", "\nEXTRA ")
        }
        if (st.startsWith("=*161*")) {
            st = st.replace("=*161*TxId:", "\nTRANSACTION_ID ")
            st = st.replace("*S*Your payment of ", "\nAMOUNT ")
            st = st.replace(" RWF to ", "\nRECEIVER ")
            st = st.replace(" has been completed at ", "\nTIME ")
            st = st.replace(". Message: ", "\nMESSAGE ")
            st = st.replace(". Your new balance: ", "\nBALANCE ")
            st = st.replace(" RWF. Fee was ", "\nFEE ")
            st = st.replace(" RWF. Coupon amount was ", "\nCOUPON ")
            st = st.replace(". External Transaction Id: ", "\nEXTERNAL_TX_ID ")
            st = st.replace(".*EN#", "\nEXTRAS ")
        }
        if (st.startsWith("*164*S")) {
            st = st.replace("*164*S*Y'ello,A transaction of ", "\nAMOUNT ")
            st = st.replace(" RWF by ", "\nRECEIVER ")
            st = st.replace(" on your MOMO account was successfully completed at ", "\nTIME ")
            st = st.replace(". Message from debit receiver: ", "\nMESSAGE ")
            st = st.replace(". Your new balance:", "\nBALANCE ")
            st = st.replace(" RWF. Fee was ", "\nFEE ")
            st = st.replace(" RWF. Financial Transaction Id: ", "\nFINANCIAL_TX_ID ")
            st = st.replace(". External Transaction Id: ", "\nEXTERNAL_TX_ID ")
            st = st.replace(".*EN#", "\nEXTRAS ")
        }
        st = "TYPE PAYMENT$st"
        return st
    }

    fun parseMessage(msg: String) : Message {
        var msg1 = ""
        if (messageType(msg) == Type.PAYMENT) msg1 = parsePayment(msg)
        if (messageType(msg) == Type.DEPOSIT) msg1 = parseDeposit(msg)
        if (messageType(msg) == Type.RECEIVING) msg1 = parseReceiving(msg)
        if (messageType(msg) == Type.TRANSFER) msg1 = parseTransfer(msg)
        if (messageType(msg) == Type.WITHDRAWAL) msg1 = parseWithdrawal(msg)
        if (messageType(msg) == Type.OTHER) Log.d("READ_MOMO", msg)
        val r = toDict(msg1)
        Log.d("READ_MOMO", r.toString())

        return r
    }

    private fun messageType(msg: String): Type {
        if (msg.startsWith("=*161*")) return Type.PAYMENT
        if (msg.startsWith("*162*")) return Type.PAYMENT
        if (msg.startsWith("*164*S*")) return Type.PAYMENT

        // ok.
        if (msg.startsWith("*165*S*")) return Type.TRANSFER
        if (msg.startsWith("You have transferred")) return Type.TRANSFER

        // ok.
        if (msg.startsWith("*153*TxId:")) return Type.WITHDRAWAL

        // ok.
        if (msg.startsWith("*165*R*")) return Type.RECEIVING
        if (msg.startsWith("*134*R*")) return Type.RECEIVING

        // ok.
        return if (msg.startsWith("*135*R*")) Type.DEPOSIT else Type.OTHER
    }

    /*
    fun starters() {
        val inputStream: InputStream = getResources().openRawResource(R.raw.momo)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val num = 0
        Log.d("READ_MOMO", "STARTING...")
        try {
            var eachline = bufferedReader.readLine()
            while (eachline != null) {
                if (messageType(eachline) == Type.PAYMENT) parseMessage(eachline)
                eachline = bufferedReader.readLine()
            }
        } catch (e: Exception) {
            Log.e("", "ERROR!")
        }
        Log.d("READ_MOMO", "DONE.")
    }*/
}
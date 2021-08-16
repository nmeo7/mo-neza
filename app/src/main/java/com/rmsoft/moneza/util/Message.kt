package com.rmsoft.moneza.util

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

open class Message : RealmObject() {
    @PrimaryKey
    var hash: String? = null
    var text: String? = null
    var textHash: String? = null
    var amount = 0
    var balance = 0
    var fee = 0
    var type: String? = null
    var time = 0L
    var timeTxt: String? = null
    var transactionId: String? = null
    var subject: String? = null
    var subjectNumber: String? = null
    var message: String? = null

    fun setContent(key: String, value : String)
    {
        if (key == "TRANSACTION_ID")
            transactionId = value
        if (key == "AGENT")
            subject = value
        if (key == "AMOUNT")
            amount = value.toInt()
        if (key == "TIME")
        {
            time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(value)?.time!!
        }
        if (key == "BALANCE")
            balance = value.toInt()
        if (key == "MESSAGE")
            message = value
        if (key == "TYPE")
            type = value

        if (key == "FEE")
            fee = value.toInt()

        if (key == "RECIPIENT" || key == "SENDER" || key == "SERVICE")
        {
            val s = value.split(" (250")
            subject = s[0]

            if (s.size > 1)
                subjectNumber = "0" + s[1].replace(")", "")
        }
    }

    fun makeHash (message : String) {

        val md = MessageDigest.getInstance("MD5")
        hash = BigInteger(
                1,
                md.digest(message.toByteArray())).toString(16).padStart(32,
                '0')
    }

    override fun toString(): String {
        return ">" +
                "id='" + hash + '\'' +
                ", text='" + text + '\'' +
                ", textHash='" + textHash + '\'' +
                ", amount=" + amount +
                ", balance=" + balance +
                ", fee=" + fee +
                ", type='" + type + '\'' +
                ", time=" + time +
                ", timeTxt='" + timeTxt + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", subject='" + subject + '\'' +
                ", subjectNumber='" + subjectNumber + '\'' +
                ", message='" + message + '\''
    }

    fun getDay () : String
    {
        if (type == "DAY")
            return subject!!

        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val today = format.format(System.currentTimeMillis())
        val yesterday = format.format(System.currentTimeMillis() - 3600 * 1000 * 24)
        val day = format.format(date)

        if (day == today)
            return "Uno munsi"
        if (day == yesterday)
            return "Ejo hashize"
        return format.format(date)
    }

    fun getTime () : String {
        val date = Date(time)
        val format = SimpleDateFormat("hh:mm", Locale.US)
        return format.format(date)
    }

    fun parseAmount (num : Number) : String {
        var ret = ""

        for (i in num.toString().indices)
        {
            if (i % 3 == 0)
                ret = " $ret";

            ret = num.toString()[num.toString().length - i - 1].toString() + ret
        }

        return ret.trim()
    }
}
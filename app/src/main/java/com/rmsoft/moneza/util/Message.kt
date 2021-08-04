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
    fun setContent(text: String?, textHash: String?, amount: Int, balance: Int,
                   fee: Int, type: String?, time: Long, timeTxt: String?, transactionId: String?,
                   subject: String?, subjectNumber: String?, message: String?) {
        hash = "id2"
        this.text = text
        this.textHash = textHash
        this.amount = amount
        this.balance = balance
        this.fee = fee
        this.type = type
        this.time = time
        this.timeTxt = timeTxt
        this.transactionId = transactionId
        this.subject = subject
        this.subjectNumber = subjectNumber
    }

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
        if (key == "RECIPIENT")
            subject = value

        if (key == "SENDER")
            subject = value
        if (key == "SERVICE")
            subject = value
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
}
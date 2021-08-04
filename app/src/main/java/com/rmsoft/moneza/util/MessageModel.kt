package com.rmsoft.moneza.util

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class MessageModel : RealmObject() {
    @PrimaryKey
    var hash: String? = null
    var text: String? = null
    var textHash: String? = null
    var amount = 0
    var balance = 0
    var fee = 0
    var type: String? = null
    var time = 0
    var timeTxt: String? = null
    var transactionId: String? = null
    var subject: String? = null
    var subjectNumber: String? = null
    var message: String? = null
    fun setContent(
        text: String?,
        textHash: String?,
        amount: Int,
        balance: Int,
        fee: Int,
        type: String?,
        time: Int,
        timeTxt: String?,
        transactionId: String?,
        subject: String?,
        subjectNumber: String?,
        message: String?
    ) {
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

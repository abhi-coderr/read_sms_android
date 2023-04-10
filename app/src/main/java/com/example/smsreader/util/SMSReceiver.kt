package com.example.smsreader.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.widget.Toast


class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val myBundle: Bundle? = p1?.extras
        var messages: Array<SmsMessage?>? = null
        var strMessage: String? = ""

        if (myBundle != null) {
            val pdus = myBundle["pdus"] as Array<Any>?
            messages = arrayOfNulls(pdus!!.size)
            for (i in messages.indices) {
                messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                strMessage += "SMS From: " + messages[i]?.getOriginatingAddress()
                strMessage += " : "
                strMessage += messages[i]?.getMessageBody()
                strMessage += "\n"
            }
            Toast.makeText(p0, strMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
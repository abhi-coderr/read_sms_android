package com.example.smsreader.util

import android.app.job.JobParameters
import android.app.job.JobService
import android.provider.Telephony
import android.util.Log
import com.example.smsreader.adapter.RecyclerAdapter
import com.example.smsreader.model.SMS
import com.example.smsreader.ui.MainActivity

class ExampleJobScheduler : JobService() {

    private val TAG = "ExampleJobServiceAbhi"
    private var jobCancelled: Boolean = false

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "job started")
        params?.let {
            doBackGroundWork(params = it)
        }
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Log.d(TAG, "Job Cancelled before execution")
        jobCancelled = true
        return false
    }

    private fun doBackGroundWork(params: JobParameters) {
        Thread(Runnable {
//            for (i in 0..9) {
//                Log.d(TAG, "run: $i")
//                if (jobCancelled) {
//                    return@Runnable
//                }
//                try {
//                    Thread.sleep(1000)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }

            val list = ArrayList<SMS>()

            val numberCol = Telephony.TextBasedSmsColumns.ADDRESS
            val textCol = Telephony.TextBasedSmsColumns.BODY
            val typeCol = Telephony.TextBasedSmsColumns.TYPE // 1 - Inbox, 2 - Sent

            val projection = arrayOf(numberCol, textCol, typeCol)

            val cursor = contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                projection, null, null, null
            )

            val numberColIdx = cursor!!.getColumnIndex(numberCol)
            val textColIdx = cursor.getColumnIndex(textCol)
            val typeColIdx = cursor.getColumnIndex(typeCol)

            while (cursor.moveToNext()) {
                val number = cursor.getString(numberColIdx)
                val text = cursor.getString(textColIdx)
                val type = cursor.getString(typeColIdx)

                val sms = SMS(number, text, type)

                sms.let {
                    list.add(sms)
                }
            }

            Log.d(TAG, "sms counts : ${list.size}")

            Log.d(TAG, "Job finished")
            jobFinished(params, false)
        }).start()
    }
}
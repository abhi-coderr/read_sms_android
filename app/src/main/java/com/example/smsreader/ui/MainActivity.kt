package com.example.smsreader.ui

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smsreader.R
import com.example.smsreader.adapter.RecyclerAdapter
import com.example.smsreader.databinding.ActivityMainBinding
import com.example.smsreader.model.SMS
import com.example.smsreader.util.ExampleJobScheduler

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val TAG = "MainActivity"

    private val permission: String = android.Manifest.permission.READ_SMS

    private val requestCode: Int = 1

    private lateinit var recyclerAdapter: RecyclerAdapter

    val list = ArrayList<SMS>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            setUpRecycler()
            readSms()
        }

    }

    override fun onResume() {
        super.onResume()
//        Handler(Looper.getMainLooper()).postDelayed({
        scheduleJob()
//        },2000)

    }

    private fun setUpRecycler() {
        recyclerAdapter = RecyclerAdapter()
        binding.smsRecyclerView.adapter = recyclerAdapter
        binding.smsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("SetTextI18n")
    fun readSms() {
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

            recyclerAdapter.sms = list

            Log.d("MY_APP", "$number $text $type")
        }

        Toast.makeText(this, "${recyclerAdapter.itemCount}", Toast.LENGTH_SHORT).show()

        cursor.close()
    }

    fun countOfSMS() {
        val count = recyclerAdapter.itemCount
        Log.d("SMSCOUNT----------->>>>>>>", count.toString())
    }

    fun scheduleJob() {
        try {
            val componentName = ComponentName(this, ExampleJobScheduler::class.java)
            val info = JobInfo.Builder(123, componentName)
                .setRequiresCharging(true)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build()
            val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            val resultCode = scheduler.schedule(info)
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "Job Scheduled")
            } else {
                Log.d(TAG, "Job scheduling failed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelJob() {
        val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(123)
        Log.d(TAG, "Job cancelled")
    }

}
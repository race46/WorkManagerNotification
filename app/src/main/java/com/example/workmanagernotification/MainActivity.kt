package com.example.workmanagernotification

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.ui.AppBarConfiguration
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmanagernotification.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() , AdapterView.OnItemClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val list = arrayListOf<String>()
    lateinit var veriAdaptoru : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        val workRequests : PeriodicWorkRequest = PeriodicWorkRequestBuilder<ShowNoti>(15,TimeUnit.MINUTES).addTag("work-work").build()

        WorkManager.getInstance(this).getWorkInfosByTagLiveData("work-work").observe(this, Observer {
            if(it.size == 0){
                WorkManager.getInstance(this).enqueue(workRequests)
            }else{
                println(it[0].state)
            }
        })
//        WorkManager.getInstance(this).enqueue(workRequests)

        val kelimeler = getWordList()
        for(i in kelimeler)if(i!="")list.add(i)


        val listemiz: ListView = binding.content.listview
        listemiz.onItemClickListener = this
        veriAdaptoru =  ArrayAdapter<String>(this, R.layout.simple_list_item_1, R.id.text1, list)
        listemiz.setAdapter(veriAdaptoru)

        binding.content.button.setOnClickListener{
            var word = binding.content.word.text.toString()
            if(word == "") word = " "
            binding.content.word.setText("")
            var desc = binding.content.description.text.toString()
            if(desc=="") desc = " "
            binding.content.description.setText("")
            list.add("$word|$desc");

            veriAdaptoru.notifyDataSetChanged()
            binding.content.word.requestFocus()

        }
        binding.content.word.requestFocus()

    }



    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "getString(R.string.channel_name)"
            val descriptionText = "getString(R.string.channel_description)"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getWordList(): List<String> {
        val sp = applicationContext.getSharedPreferences("com.example.workmanagernotification",Context.MODE_PRIVATE)
        return sp.getString("words","")!!.split("&")
    }

    override fun onStop() {
        super.onStop()
        val sp = applicationContext.getSharedPreferences("com.example.workmanagernotification",Context.MODE_PRIVATE)
        val edit = sp.edit()
        var words = ""
        for (i in list) if(i!="") words+=i+"&"
        edit.putString("words",words)
        edit.apply()
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        list.removeAt(p2)
        veriAdaptoru.notifyDataSetChanged()
    }
}
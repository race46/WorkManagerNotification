package com.example.workmanagernotification

import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*
import kotlin.collections.ArrayList

class ShowNoti(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {

        val r = Random()
        val list = getShared()
        if(list.size==0) return Result.success()
        val index = r.nextInt(list.size)


        noti(list[index])
        return Result.success()
    }

    fun noti(data:String){
        val d1 = data.split("|")[0]
        val d2 = data.split("|")[1]

        var builder = NotificationCompat.Builder(applicationContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.img)
            .setContentTitle(d1)
            .setContentText(d2)
            .setPriority(NotificationCompat.PRIORITY_MAX)


        with(NotificationManagerCompat.from(applicationContext)) {
            notify(0, builder.build())
        }
    }

    fun getShared(): ArrayList<String> {
        val sp = applicationContext.getSharedPreferences("com.example.workmanagernotification",Context.MODE_PRIVATE)
        val kelimeler =  sp.getString("words","")!!.split("&")
        val list = arrayListOf<String>()
        for(i in kelimeler)if(i!="")list.add(i)
        return list
    }

}
package com.example.notifications_solution

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.xml.transform.Source

class ViewModel(app: Application): AndroidViewModel(app) {



    lateinit var manager: NotificationManager

    lateinit var builder : Notification.Builder

    private var prefs = app.getSharedPreferences("com.example.notifications_solution", Context.MODE_PRIVATE)

    private val second:Long = 1_000L

    private val minute:Long = 60_000L

    var handler = Handler()

    var runnable = object :Runnable{
        override fun run() {

        }

    }

    private var countDownTime:Long = 0



    // TODO: Step 1.2 setting my_ischecked

    private  val is_checked = MutableLiveData<Boolean>()

    // TODO: Step 1.3 initial my_ischecked(MutableLiveData) and init_notifiAndBuilder(function)

    init {

        is_checked .value = false

        init_notifiAndBuilder(app.applicationContext)



    }


    // TODO: Step 1.4 setting my_ischecked


    fun is__checked_set(checked:Boolean){

        is_checked.value = checked

    }


    fun is_checked_get(): LiveData<Boolean> {

        return is_checked

    }

    // TODO: Step 2 setting elapsedTime


    private val elapsedTime = MutableLiveData<String>()


    fun elapsedTime_get(): LiveData<String>{

        return elapsedTime

    }




   val timeOption = app.resources.getIntArray(R.array.times)

   val timeSelection  = MutableLiveData<Long>()



    fun timeSelection_set(position:Int){


        if (timeOption[position].toLong() != 0L){

            timeSelection.value = timeOption[position].toLong()* 60_000

            viewModelScope.launch {

                save_time(timeSelection.value!!)


            }



        }

        else{

            timeSelection.value = 10_000

        }


    }

    fun timeSelection_get(): LiveData<Long> {

         return  timeSelection

    }





    // TODO: Step 3 setting my_notifi


    fun set_notifi(){


        when (is_checked.value){

            true -> startTimer()


            false -> stopTimer()


        }


    }



    private  fun cancel_notifi() {

        manager.cancelAll()

    }


    private  fun send_notifi() {

        manager.notify(0,builder.build())

    }





    private fun startTimer(){

        set_time()

        viewModelScope.launch {

            runnable = object :Runnable{

                override fun run() {

                    handler.postDelayed(runnable,1000)

                    timeSelection.value = timeSelection.value!! - 1000

                    set_time()

                    if(timeSelection.value!! <= 0){

                        handler.removeCallbacks(runnable)

                        send_notifi()

                        resetTimer()


                    }


                }


            }

            handler.postDelayed(runnable,1000)


        }


    }



    private fun stopTimer(){


        handler.removeCallbacks(runnable)

        viewModelScope.launch {

        timeSelection.value = load_time()

        }

        elapsedTime.value = "00:00"

        is_checked.value = false

    }

    private fun resetTimer(){

        is_checked.value = false

    }



    // TODO: Step 1.1 make my_notification

private fun init_notifiAndBuilder(context: Context){


        manager =  ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("Cooking_Egg", "Cooking_Egg", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
            builder = Notification.Builder(context, "Cooking_Egg")
        } else {
            builder = Notification.Builder(context)
        }

        builder.setSmallIcon(R.drawable.egg_icon)
            .setContentTitle("Cooking_Egg")
            .setContentText("Hello")
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.cooked_egg))
            .setAutoCancel(true)

    }



 private suspend fun save_time(time:Long) =
         withContext(Dispatchers.IO) {
         prefs.edit().putLong("TRIGGER_TIME", time).apply()
             }


  private suspend fun load_time():Long =
            withContext(Dispatchers.IO) {
                prefs.getLong("TRIGGER_TIME", 0)
            }





 private fun set_time(){


     val minus = timeSelection.value!! / 60_000

     val second = timeSelection.value!! % 60_000 / 1000

         if(minus < 10){

            if(second < 10){

           elapsedTime.value = "0$minus:0$second"

         }

         else{

            elapsedTime.value =  "0$minus:$second"

         }


       }

         else{

            if(second < 10){

           elapsedTime.value =  "$minus:0$second"

         }

            else{

            elapsedTime.value =  "$minus:$second"

         }

         }





}

}
package com.example.notifications_solution

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)


        // TODO: Step 2.2 讓我的switch 初使值 ＝ is_checked 的初始值


        val is_checkedObeserver = Observer<Boolean> {

            switch1.isChecked = it

        }

        viewModel.is_checked_get().observe(this,is_checkedObeserver)



        val elapsedTimeObeserver = Observer<String> {


            textView.text = it

        }

        viewModel.elapsedTime_get().observe(this,elapsedTimeObeserver)



        switch1.setOnClickListener {

            // TODO: Step 2.3 is_checked 的值 ＝ 我的switch 值

            val checked = switch1.isChecked

            viewModel.is__checked_set(checked)

            // TODO: Step 2.4 根據我 is_checked的值 去決定要不要送出notifi

            viewModel.set_notifi()

        }




       spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

           override fun onNothingSelected(parent: AdapterView<*>?) {

           }

           override fun onItemSelected(
               parent: AdapterView<*>?,
               view: View?,
               position: Int,
               id: Long
           ) {
               viewModel.timeSelection_set(position)
           }


       }


    }


}
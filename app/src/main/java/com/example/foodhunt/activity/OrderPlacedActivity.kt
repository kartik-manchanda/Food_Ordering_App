package com.example.foodhunt.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import com.example.foodhunt.R

class OrderPlacedActivity : AppCompatActivity() {

    lateinit var btnOkay: Button

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        btnOkay = findViewById(R.id.btnOkay)

        btnOkay.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)

            finishAffinity()//finish all the activities
        })
    }

    override fun onBackPressed() {
        //force user to press okay button to take him to dashboard screen
        //user can't go back
    }


}


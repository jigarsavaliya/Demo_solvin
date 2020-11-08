package com.demo.myapplication.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.myapplication.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

}
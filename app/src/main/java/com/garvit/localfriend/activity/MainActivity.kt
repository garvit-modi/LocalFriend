package com.garvit.localfriend.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.garvit.localfriend.R
import com.garvit.localfriend.databinding.ActivityMainBinding
import com.garvit.localfriend.utils.startAActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this , R.layout.activity_main)
        startAActivity(SignUpActivity::class.java)
    }
}
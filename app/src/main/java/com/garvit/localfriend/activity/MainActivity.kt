package com.garvit.localfriend.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.garvit.localfriend.R
import com.garvit.localfriend.adapter.ViewPagerAdapter
import com.garvit.localfriend.databinding.ActivityMainBinding
import com.garvit.localfriend.utils.BaseActivity
import com.garvit.localfriend.utils.startAActivity
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity<ActivityMainBinding>() {
    val pager by lazy {
        findViewById<ViewPager2>(R.id.viewPager)
    }


    override fun getLayoutResId(): Int  = R.layout.activity_main

    override fun setupViews() {
        setSupportActionBar(findViewById(R.id.toolbar))
        pager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(
            findViewById(R.id.tabs),
            pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, pos ->
                when (pos) {
                    0 -> tab.text = "Chats"
                    1 -> tab.text = "Users"
                }
            }).attach()
    }

    override fun setupViewsOnResume() {

    }
}
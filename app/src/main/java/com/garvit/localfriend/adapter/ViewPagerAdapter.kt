package com.garvit.localfriend.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.garvit.localfriend.fragment.ChatFragment
import com.garvit.localfriend.fragment.UserFragment

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ChatFragment()
        else -> UserFragment()
    }

}
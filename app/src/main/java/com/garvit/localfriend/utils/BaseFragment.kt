package com.garvit.localfriend.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.garvit.localfriend.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren

abstract class BaseFragment <B : ViewDataBinding> : Fragment() {

    protected lateinit  var dataBinding: B

    override fun onDetach() {
        super.onDetach()

    }
    abstract fun setupViews()

    abstract fun setupViewsOnResume()

    @LayoutRes
    abstract fun getLayoutId(): Int



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)


        setupViews()
        return dataBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onResume() {
        super.onResume()
        setupViewsOnResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }


    fun isViewLive() = isAdded && view != null

    protected open fun initViews() {

    }

    protected fun openUrl(url: String) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: ActivityNotFoundException) {
        }
    }


    fun refer() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TITLE, R.string.app_name)
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Stylish Fonts express your LOVE with Stylish Fonts @ Whatsapp Facebook any chat app. I have been using it in a while, give it a try  : https://play.google.com/store/apps/details?id=${activity?.packageName}&h=en")
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }



    fun moreApps() {
        val more = Intent(Intent.ACTION_VIEW)
        more.data = Uri.parse("market://search?q=pub:" +"")
        startActivity(more)
    }
    
}
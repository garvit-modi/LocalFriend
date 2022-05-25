package com.garvit.localfriend.utils

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.support.multidex.MultiDex
import android.view.Window
import android.widget.Toast
import com.chuckerteam.chucker.api.Chucker
import com.garvit.localfriend.R
import com.google.firebase.FirebaseApp


class   MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        if (BuildConfig.DEBUG) {
//            Stetho.initializeWithDefaults(this)
//        }
        val spPrivate = getSharedPreferences("private", MODE_PRIVATE)
        tinyDB = TinyDB(spPrivate)
        instance = this

        Chucker.getLaunchIntent(this)


    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }



    companion object {
        lateinit var tinyDB: TinyDB


        @get:Synchronized
        var instance: MyApplication? = null
            private set
        var dialog: Dialog? = null
        fun createLoaderView(context: Context) {

            dialog = Dialog(context)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.window?.decorView?.setBackgroundResource(android.R.color.transparent)
            dialog!!.setCancelable(false)
            dialog!!.setContentView(R.layout.loader_view_layout)
            dialog!!.show()
        }

        fun ProgressBar(context: Activity, flag: Boolean) {
            try {
                if (flag) {
                    dialog?.let {
                        if (it.isShowing) {
                            it.dismiss()
                        }
                    }
                    createLoaderView(context)

                } else {
                    dialog?.dismiss()
                }
            } catch (e: Exception) {

            }
        }

        fun ProgressBar(flag: Boolean) {
            if (dialog != null) {
                dialog!!.dismiss()
            }
        }


        fun Context.toast(message: CharSequence) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }


    }
}
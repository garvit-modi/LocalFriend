package com.garvit.localfriend.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.concurrent.schedule


fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun <A : Activity> Activity.startClearTopActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(it)
    }
}

fun <A : Activity> Activity.startAActivity(activity: Class<A>) {
    Intent(this, activity).also {
        startActivity(it)
    }
}


fun <A : Activity> Activity.startAActivity(activity: Class<A>, data: String) {
    Intent(this, activity).putExtra("data", data).also {
        startActivity(it)
    }
}

fun <A : Activity> Activity.startAActivity(activity: Class<A>, data: Boolean) {
    Intent(this, activity).putExtra("data", data).also {
        startActivity(it)
    }
}

@ColorInt
fun Context.getColorFromAttr( @AttrRes attrColor: Int
): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
    val textColor = typedArray.getColor(0, 0)
    typedArray.recycle()
    return textColor
}


fun Context.setAppLocale(language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    return createConfigurationContext(config)
}


fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.setonclick(onclick: (flag: Boolean) -> Unit) {
    this.isEnabled = false
    Timer().schedule(3000) {
//        runOnUiThread(Runnable {
//            // Stuff that updates the UI
//        })
        onclick.invoke(true)
        this@setonclick.isEnabled = true
    }
    this.setOnClickListener {

    }
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun snackbar(message: String, context: View, action: (() -> Unit)? = null) {
    var mess = message
    if (message.isNullOrEmpty()) mess = "Bad Request"
    val snackbar = Snackbar.make(context, mess, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}





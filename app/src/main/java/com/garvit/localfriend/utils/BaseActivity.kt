package com.garvit.localfriend.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.app.resourcemodule.utils.Constants
import com.garvit.localfriend.R
import java.util.*


abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var dataBinding: B


    val RecordAudioRequestCode = 1
    private var speechRecognizer: SpeechRecognizer? = null


    override fun attachBaseContext(newBase: Context) {
        //langugae code
        super.attachBaseContext(
            LocaleHelper.wrap(
                newBase,
                MyApplication.tinyDB.getString(Constants.SharedPref.LANG) ?: "en"
            )
        )
    }

    @LayoutRes
    abstract fun getLayoutResId(): Int
    abstract fun setupViews()

    abstract fun setupViewsOnResume()



    override fun onResume() {
        super.onResume()
        setupViewsOnResume()
    }

    open fun getMyContext(): Context? {
        return LocaleHelper.wrap(
            this,
            MyApplication.tinyDB.getString(Constants.SharedPref.LANG) ?: "en"
        )
    }

    override fun getBaseContext(): Context {
        return getMyContext()!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //theme code


        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, getLayoutResId())

//        val context: Context =
//            MyContextWrapper.wrap(this /*in fragment use getContext() instead of this*/, "fr")
//        resources.updateConfiguration(
//            context.resources.configuration,
//            context.resources.displayMetrics
//        )
        setupViews()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setStatusBarGradiant(activity: Activity, background: Drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.navigationBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }

    private fun takeToPlaystore(packageName: String?) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            val url = "https://play.google.com/store/apps/details?id=$packageName"
            openUrl(url)
        }
    }

    private fun openUrl(url: String) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No related activity found", Toast.LENGTH_SHORT).show()
        }
    }




    fun speechTotext(editText : EditText) {
        var  x = 0
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        else
        {
            val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

            speechRecognizer!!.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(bundle: Bundle) {}
                override fun onBeginningOfSpeech() {
                    editText.setText("")
                    editText.setHint("Listening...")
                }

                override fun onRmsChanged(v: Float) {}
                override fun onBufferReceived(bytes: ByteArray) {}
                override fun onEndOfSpeech() {}
                override fun onError(i: Int) {}
                override fun onResults(bundle: Bundle) {
//                micButton.setImageResource(R.drawable.ic_mic_black_off)
                    val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    editText.setText(data!![0])
                }

                override fun onPartialResults(bundle: Bundle) {}
                override fun onEvent(i: Int, bundle: Bundle) {}
            })

            speechRecognizer!!.startListening(speechRecognizerIntent)
        }

    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RecordAudioRequestCode
            )
        }



    }

    fun getDeviceId():String{
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.let { it.destroy() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
                this,
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }



}
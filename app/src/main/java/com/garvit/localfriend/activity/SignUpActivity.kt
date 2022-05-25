package com.garvit.localfriend.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.app.resourcemodule.utils.Constants.SharedPref.Companion.PHONE_NUMBER
import com.garvit.localfriend.R
import com.garvit.localfriend.databinding.ActivitySignUpBinding
import com.garvit.localfriend.utils.BaseActivity
import com.hbb20.CountryCodePicker

class SignUpActivity : BaseActivity<ActivitySignUpBinding>() {


    override fun getLayoutResId(): Int  = R.layout.activity_sign_up

    override fun setupViews() {

    }

    override fun setupViewsOnResume() {
       dataBinding.apply {
           phoneNumberEt.addTextChangedListener {value ->
               nextBtn.isEnabled = !(value.isNullOrEmpty() || value.length < 10)
           }
           nextBtn.setOnClickListener {
               checkNumber()
           }
       }
    }

    private fun checkNumber() {
        startActivity(Intent(this, OTPActivity::class.java).putExtra(PHONE_NUMBER, dataBinding.ccp.selectedCountryCodeWithPlus + dataBinding.phoneNumberEt.text.toString()))
    }
}
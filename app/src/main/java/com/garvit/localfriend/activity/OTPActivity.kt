package com.garvit.localfriend.activity

import `in`.aabhasjindal.otptextview.OTPListener
import android.os.CountDownTimer
import android.util.Log
import androidx.core.view.isVisible
import com.app.resourcemodule.utils.Constants
import com.app.resourcemodule.utils.Constants.SharedPref.Companion.TAG
import com.garvit.localfriend.R
import com.garvit.localfriend.databinding.ActivityOtpBinding
import com.garvit.localfriend.utils.BaseActivity
import com.garvit.localfriend.utils.MyApplication
import com.garvit.localfriend.utils.startNewActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OTPActivity : BaseActivity<ActivityOtpBinding>() {
    private lateinit var phoneNumber: String
    private lateinit var auth: FirebaseAuth

    private lateinit var mCounter: CountDownTimer
    private lateinit var otp: String
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    override fun getLayoutResId(): Int = R.layout.activity_otp

    override fun setupViews() {
        MyApplication.ProgressBar(true)
        auth = Firebase.auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                dataBinding.otpTv.otp = credential.smsCode
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
                MyApplication.ProgressBar(false)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                MyApplication.ProgressBar(false)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Invalid Phone Number.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Snackbar.make(
                        findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    notifyUserAndRetry("Your Phone Number might be wrong or connection error.Retry again!")
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")
                MyApplication.ProgressBar(false)
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }
        dataBinding.apply {

            dataBinding.otpTv.otpListener = object : OTPListener {
                override fun onInteractionListener() {
                    dataBinding.verificationBtn.alpha = 0.5f
                    dataBinding.verificationBtn.isEnabled = false
                }

                override fun onOTPComplete(_otp: String) {
                    otp = _otp
                    // fired when user has entered the OTP fully.
                    dataBinding.verificationBtn.alpha = 1f
                    dataBinding.verificationBtn.isEnabled = true
                }
            }

            resendBtn.setOnClickListener {
                MyApplication.ProgressBar(true)
                resendVerificationCode(
                    intent.getStringExtra(Constants.SharedPref.PHONE_NUMBER)!!,
                    resendToken
                )
            }
            verificationBtn.setOnClickListener {
                verifyPhoneNumberWithCode(storedVerificationId, otp)
            }
        }

        startPhoneNumberVerification(intent.getStringExtra(Constants.SharedPref.PHONE_NUMBER)!!)
        startCounter(60000)
    }

    private fun notifyUserAndRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                startNewActivity(SignUpActivity::class.java)
            }

            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            setCancelable(false)
            create()
            show()
        }
    }

    private fun startCounter(time: Long) {
        dataBinding.apply {
            resendBtn.isEnabled = false
            counterTv.isVisible = true
            mCounter = object : CountDownTimer(time, 1000) {
                override fun onFinish() {
                    resendBtn.isEnabled = true
                    counterTv.isVisible = false
                }

                override fun onTick(timeLeft: Long) {

                    counterTv.text = "Seconds Remaining : " + timeLeft / 1000
                }

            }.start()
        }
    }

    override fun setupViewsOnResume() {

    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private fun updateUI(user: FirebaseUser? = auth.currentUser) {

    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]


    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
        // [END verify_with_code]
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        MyApplication.ProgressBar(false)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    override fun onBackPressed() {


    }
}
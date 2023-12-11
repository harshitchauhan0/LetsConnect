package com.harshit.letsconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.harshit.letsconnect.databinding.ActivitySignUpBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var timing = 60L
    private lateinit var phoneNumber:String
    private lateinit var signUpActivitybinding:ActivitySignUpBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var verificationCode:String
    private lateinit var token: PhoneAuthProvider.ForceResendingToken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpActivitybinding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        signUpActivitybinding.progressbar.visibility = View.GONE

        signUpActivitybinding.countryCodeHolder.registerCarrierNumberEditText(signUpActivitybinding.number)
        signUpActivitybinding.generateBTN.setOnClickListener {
            if(signUpActivitybinding.countryCodeHolder.isValidFullNumber){
                phoneNumber = signUpActivitybinding.countryCodeHolder.fullNumberWithPlus
                sendOTP(false)
            }
            else{
                Toast.makeText(applicationContext,"Phone Number is valid",Toast.LENGTH_LONG).show()
            }
        }

        signUpActivitybinding.submitBTN.setOnClickListener {
            val otpTyped = signUpActivitybinding.otpET.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationCode,otpTyped)
            signIn(credential)
        }

        signUpActivitybinding.resendTV.setOnClickListener {
            sendOTP(true)
        }
    }

    private fun sendOTP(isResend: Boolean){
        setInProgress(true)
        startResendTimer()
        val builder = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber)
            .setTimeout(60L,TimeUnit.SECONDS).setActivity(this).setCallbacks(object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    signIn(p0)
                    setInProgress(false)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.v("TAG",p0.message.toString())
                    Toast.makeText(applicationContext,"OTP VERIFICATION FAILED",Toast.LENGTH_LONG).show()
                    setInProgress(false)
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    verificationCode = p0
                    token = p1
                    Toast.makeText(applicationContext,"OTP SENT",Toast.LENGTH_LONG).show()
                    setInProgress(false)
                }

            })

        if(isResend){
            Toast.makeText(applicationContext,"Resensding",Toast.LENGTH_LONG).show()
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(token).build())
        }
        else{
            Toast.makeText(applicationContext,"New ",Toast.LENGTH_LONG).show()
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        }
    }

    private fun startResendTimer() {
        val scope = CoroutineScope(Dispatchers.Main)
        var timing = 60L

        signUpActivitybinding.resendTV.isEnabled = false

        scope.launch {
            while (timing > 0) {
                timing--
                signUpActivitybinding.resendTV.text = "Resend in $timing s"
                delay(1000) // Wait for 1 second using coroutine delay

                if (timing == 0L) {
                    signUpActivitybinding.resendTV.isEnabled = true
                }
            }
        }
    }

    private fun signIn(credential: PhoneAuthCredential) {
        setInProgress(true)
        auth.signInWithCredential(credential).addOnCompleteListener {
            setInProgress(false)
            if(it.isSuccessful){
                Toast.makeText(applicationContext,"OTP Verification done",Toast.LENGTH_LONG).show()
                startActivity(Intent(this,SetUserNameActivity::class.java).putExtra("phone",phoneNumber))
            }
            else{
                Toast.makeText(applicationContext,"OTP Verification failed",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setInProgress(isProgress: Boolean){
        if(isProgress){
            signUpActivitybinding.progressbar.visibility = View.VISIBLE
            signUpActivitybinding.submitBTN.visibility = View.GONE
        }
        else{
            signUpActivitybinding.progressbar.visibility = View.GONE
            signUpActivitybinding.submitBTN.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser!=null){
            startActivity(Intent(this,SetUserNameActivity::class.java))
            finish()
        }
    }

}
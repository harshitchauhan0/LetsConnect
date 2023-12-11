package com.harshit.letsconnect

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class IntroductionActivity : AppCompatActivity() {
    private lateinit var  progressBar:ProgressBar
    private lateinit var auth:FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        val signUpBtn:Button = findViewById(R.id.signup)

        signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        progressBar = findViewById(R.id.progressbar)
        auth = FirebaseAuth.getInstance()
        progressBar.visibility = View.GONE
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
        if(auth.currentUser!=null){
            if(sharedPreferences.getBoolean("logged",false)){
                Toast.makeText(this,"You are logged in ",Toast.LENGTH_LONG).show()
                startActivity(Intent(this, GroupActivity::class.java))
            }
            progressBar.visibility = View.VISIBLE
            sharedPreferences.edit().putBoolean("logged",true).apply()
        }
    }
}
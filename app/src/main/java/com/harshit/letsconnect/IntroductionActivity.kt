package com.harshit.letsconnect

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.harshit.letsconnect.extrasUtils.ExtraUtils
import com.harshit.letsconnect.models.UserModel

class IntroductionActivity : AppCompatActivity() {
    private lateinit var  progressBar:ProgressBar
    private lateinit var auth:FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        val signUpBtn:Button = findViewById(R.id.signup)
        database = FirebaseFirestore.getInstance()
        if(intent.extras!=null && auth.currentUser!=null){
            // If we clicked on notification
            val userId = intent.extras!!.getString(ExtraUtils.USERID) as String
            database.collection(ExtraUtils.USERS).document(userId).get().addOnCompleteListener {
                if(it.isSuccessful){
                    val userModel = it.result.toObject(UserModel::class.java)

                    val intent = Intent(this,MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)


                    val i = Intent(this,ChatActivity::class.java)
                    i.putExtra(ExtraUtils.NAME,userModel?.getUsername())
                    i.putExtra(ExtraUtils.UID,userModel?.getUserId())
                    i.putExtra(ExtraUtils.PHONE,userModel?.getPhoneNumber())
                    i.putExtra(ExtraUtils.TIME,userModel?.getTimestamp()?.toDate())
                    i.putExtra(ExtraUtils.TOKEN,userModel?.getToken())
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                    finish()
                }
                else{
                    Log.v("TAG",it.exception.toString())
                }
            }
        }

        signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        progressBar = findViewById(R.id.progressbar)
        auth = FirebaseAuth.getInstance()
        progressBar.visibility = View.GONE
        sharedPreferences = getSharedPreferences(ExtraUtils.LOGIN, MODE_PRIVATE)
        if(auth.currentUser!=null){
            if(sharedPreferences.getBoolean(ExtraUtils.LOGGED,false)){
                Toast.makeText(this,"You are logged in ",Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
            progressBar.visibility = View.VISIBLE
            sharedPreferences.edit().putBoolean(ExtraUtils.LOGGED,true).apply()
        }
    }
}
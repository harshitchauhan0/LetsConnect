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
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
            val userId = intent.extras!!.getString("userId") as String
            database.collection("users").document(userId).get().addOnCompleteListener {
                if(it.isSuccessful){
                    val model = it.result.toObject(UserModel::class.java)

                    val intent = Intent(this,MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)


                    val i = Intent(this,ChatActivity::class.java)
                    i.putExtra("name",model?.getUsername())
                    i.putExtra("uid",model?.getUserId())
                    i.putExtra("phone",model?.getPhoneNumber())
                    i.putExtra("time",model?.getTimestamp()?.toDate())
                    i.putExtra("token",model?.getToken())
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
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
        if(auth.currentUser!=null){
            if(sharedPreferences.getBoolean("logged",false)){
                Toast.makeText(this,"You are logged in ",Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
            progressBar.visibility = View.VISIBLE
            sharedPreferences.edit().putBoolean("logged",true).apply()
        }
    }
}
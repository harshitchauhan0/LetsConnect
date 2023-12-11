package com.harshit.letsconnect

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.harshit.letsconnect.databinding.ActivitySetusernameBinding

class SetUserNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetusernameBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userID:String
    private lateinit var database: FirebaseFirestore
     private var userModel: UserModel? = null
    private lateinit var phoneNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_setusername)
        binding.progressbar.visibility = View.GONE
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userID = auth.uid!!
        phoneNumber = intent.getStringExtra("phone")!!

        getName()

        binding.letsGoBtn.setOnClickListener {
            binding.progressbar.visibility = View.VISIBLE
            setName()
        }

    }

    private fun setName() {
        setInProgress(true)
        val name = binding.userNameET.text.toString()
        if(TextUtils.isEmpty(name) or (name.length<3)){
            binding.userNameET.error = "Please enter long name"
            setInProgress(false)
            return
        }

        if(userModel!=null){
            userModel!!.userName = name
        }
        else{
            userModel = UserModel(phoneNumber,name, Timestamp.now())
        }

        database.collection("users").document(userID).set(userModel!!).addOnCompleteListener {
            setInProgress(false)
            if(it.isSuccessful){
                Toast.makeText(applicationContext,"Username done",Toast.LENGTH_LONG).show()
                val i = Intent(this, GroupActivity::class.java)
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
            }
            else{
                Toast.makeText(applicationContext,it.exception.toString(),Toast.LENGTH_LONG).show()
                Log.v("TAG",it.exception.toString())
            }
        }
    }

    private fun getName() {
        setInProgress(true)
        database.collection("users").document(userID)
            .get().addOnCompleteListener {
                setInProgress(false)
                if(it.isSuccessful){
                    userModel = it.result.toObject(UserModel::class.java)
                    if(userModel!=null){
                        binding.userNameET.setText(userModel!!.userName)
                    }
                }
                else{

                }
            }
    }

    private fun setInProgress(isProgress: Boolean){
        if(isProgress){
            binding.progressbar.visibility = View.VISIBLE
            binding.letsGoBtn.visibility = View.GONE
        }
        else{
            binding.progressbar.visibility = View.GONE
            binding.letsGoBtn.visibility = View.VISIBLE
        }
    }

}
package com.harshit.letsconnect

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.DataBindingUtil
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.harshit.letsconnect.databinding.ActivitySettingUpUserBinding
import java.util.Date

class SettingUpUser : AppCompatActivity() {
    private lateinit var binding:ActivitySettingUpUserBinding
    private lateinit var phoneNumber:String
    private lateinit var database:FirebaseFirestore
    private var userModel:UserModel? = null
    private var uid:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_setting_up_user)
        if(intent.hasExtra("phone")){
            phoneNumber = intent.getStringExtra("phone").toString()
        }
        database = FirebaseFirestore.getInstance()

        uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            Log.v("TAG","Uid is null")
        }
        getUserName()

        binding.letsGoBtn.setOnClickListener {

            setName()

        }

    }

    private fun setName() {

        val name = binding.userNameET.text.toString()
        if(name.isEmpty() or (name.length<3)){
            binding.userNameET.error = "Username is too short"
            return
        }
        setInProgress(true)
        if((userModel) !=null){
            userModel!!.setName(name)
        }
        else{
            userModel = UserModel(phoneNumber,name,Timestamp.now())
        }

        uid?.let {  i ->
            database.collection("users").document(i).set(userModel!!).addOnCompleteListener {
                setInProgress(false)
                if(it.isSuccessful){
                    Toast.makeText(applicationContext,"Username done", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
                else{

                }
            }
        }

    }

    private fun getUserName() {
        setInProgress(true)
        uid?.let { i ->
            database.collection("users").document(i).get().addOnCompleteListener {
                setInProgress(false)
                if(it.isSuccessful){
                    if(it.result.toObject(UserModel::class.java)!=null){
                        userModel = it.result.toObject(UserModel::class.java)!!
                        if(userModel!=null) {
                            binding.userNameET.setText(userModel!!.getName())
                        }
                    }
                    else{
                        Log.v("TAG","Not able to convert")
                    }

                }
                else{
                    Log.v("TAG",it.exception.toString())
                }
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
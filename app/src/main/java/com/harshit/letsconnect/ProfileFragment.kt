package com.harshit.letsconnect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.harshit.letsconnect.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var database:FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userModel: UserModel
    private lateinit var launcher: ActivityResultLauncher<String>
    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try{
                if (it != null) {
                    imageUri = it
                    Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform())
                        .into(binding.profileImg)
                }
            }catch(e:Exception){
                e.printStackTrace()
            }

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("profile").child(auth.uid!!)
        getUserData()
        binding.profileImg.setOnClickListener {
            launcher.launch("image/*")
        }
        binding.update.setOnClickListener { updateBTNClick() }
        return binding.root
    }

    private fun updateBTNClick() {
        val username = binding.profileName.text.toString()
        if(username.isEmpty() or (username.length<3)){
            binding.profileName.error = "Username is too short"
            return
        }
        userModel.setUsername(username)
        setInProgress(true)
        if(imageUri!=null){
            storageReference.putFile(imageUri!!).addOnCompleteListener { task->
                if(task.isSuccessful){
                    Toast.makeText(context,"Updated",Toast.LENGTH_LONG).show()
                    updateDatabase()
                }
                else{
                    Log.v("TAG",task.exception.toString())
                }
            }
        }
        else{
            updateDatabase()
        }
    }

    private fun updateDatabase(){
        database.collection("users").document(auth.uid!!).set(userModel).addOnCompleteListener {
            setInProgress(false)
            if(it.isSuccessful){
                Toast.makeText(activity,"Update Successful",Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(activity,"Update Failed",Toast.LENGTH_LONG).show()
                Log.v("TAG",it.exception.toString())
            }
        }
    }

    private fun getUserData() {
        setInProgress(true)
        storageReference.downloadUrl.addOnCompleteListener {
            if(it.isSuccessful){
                val uri = it.result
                Glide.with(this).load(uri).into(binding.profileImg)
            }
        }
        database.collection("users").document(auth.uid!!).get().addOnCompleteListener {
            setInProgress(false)
            if(it.isSuccessful){
                userModel = it.result.toObject(UserModel::class.java)!!
                binding.profileName.setText(userModel.getUsername())
                binding.profileNumber.setText(userModel.getPhoneNumber())

            }
        }
    }

    private fun setInProgress(isProgress: Boolean){
        if(isProgress){
            binding.progressbar.visibility = View.VISIBLE
            binding.update.visibility = View.GONE
        }
        else{
            binding.progressbar.visibility = View.GONE
            binding.update.visibility = View.VISIBLE
        }
    }

}
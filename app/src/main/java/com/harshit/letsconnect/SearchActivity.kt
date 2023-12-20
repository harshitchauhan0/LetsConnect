package com.harshit.letsconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.harshit.letsconnect.adapters.searchRecyclerViewAdapter
import com.harshit.letsconnect.databinding.ActivitySearchBinding
import com.harshit.letsconnect.extrasUtils.ExtraUtils
import com.harshit.letsconnect.models.UserModel

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: searchRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search)

        if(intent.hasExtra(ExtraUtils.NAME)){
            search(intent.getStringExtra(ExtraUtils.NAME)!!)
        }
        binding.searchUserBtn.setOnClickListener {
            val name = binding.seachUsernameInput.text.toString()
            if(TextUtils.isEmpty(name) or (name.length<3)){
                binding.seachUsernameInput.error = "Invalid Name"
            }
            else{
                search(binding.seachUsernameInput.text.toString())
            }
        }
        binding.seachUsernameInput.requestFocus()
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }


    private fun search(string: String) {

        val query = FirebaseFirestore.getInstance().collection(ExtraUtils.USERS)
            .whereGreaterThanOrEqualTo(ExtraUtils.USERNAME,string).whereLessThanOrEqualTo(ExtraUtils.USERNAME,string+"uf8ff")

        val firestoreRecyclerOptions:FirestoreRecyclerOptions<UserModel>  = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java).build()


        adapter = searchRecyclerViewAdapter(firestoreRecyclerOptions,this)
        binding.searchUserRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchUserRecyclerView.adapter = adapter
        adapter.startListening()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
}
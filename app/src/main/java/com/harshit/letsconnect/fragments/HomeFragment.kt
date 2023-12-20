package com.harshit.letsconnect.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.harshit.letsconnect.models.ChatroomModel
import com.harshit.letsconnect.extrasUtils.ExtraUtils
import com.harshit.letsconnect.adapters.HomeRecyclerViewAdapter
import com.harshit.letsconnect.R
import com.harshit.letsconnect.SearchActivity
import com.harshit.letsconnect.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var adapter: HomeRecyclerViewAdapter
    private lateinit var database:FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        database = FirebaseFirestore.getInstance()
        searchBoxConfiguration()

        setRecyclerView()


        return binding.root
    }

    private fun setRecyclerView() {
        val query = database.collection(ExtraUtils.CHATROOM)
            .whereArrayContains(ExtraUtils.USER_ID, ExtraUtils.currentUserId()!!)
            .orderBy(ExtraUtils.LAST_MESSAGE_TIMESTAMP, Query.Direction.DESCENDING)
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<ChatroomModel> = FirestoreRecyclerOptions.Builder<ChatroomModel>()
            .setQuery(query, ChatroomModel::class.java).build()


        adapter = HomeRecyclerViewAdapter(firestoreRecyclerOptions,requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
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
        adapter.notifyDataSetChanged()
    }

    private fun searchBoxConfiguration() {
        binding.searchBox.setOnClickListener {
            if(TextUtils.isEmpty(binding.seachUsernameInput.text) or (binding.seachUsernameInput.text.toString().length<3)){
                binding.seachUsernameInput.error = "Invalid Username"
            }
            else{
                val intent = Intent(activity, SearchActivity::class.java)
                intent.putExtra(ExtraUtils.NAME,binding.seachUsernameInput.text.toString())
                startActivity(intent)
            }

        }
    }
}
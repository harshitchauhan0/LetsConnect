package com.harshit.letsconnect

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.harshit.letsconnect.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false)
        searchBoxConfiguration()


        return binding.root
    }

    private fun searchBoxConfiguration() {
        binding.searchBox.setOnClickListener {
            if(TextUtils.isEmpty(binding.seachUsernameInput.text) or (binding.seachUsernameInput.text.toString().length<3)){
                binding.seachUsernameInput.error = "Invalid Username"
            }
            else{
                val intent = Intent(activity,SearchActivity::class.java)
                intent.putExtra("name",binding.seachUsernameInput.text.toString())
                startActivity(intent)
            }

        }
    }
}
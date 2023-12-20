package com.harshit.letsconnect.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.harshit.letsconnect.R
import com.harshit.letsconnect.databinding.FragmentInviteBinding
import com.harshit.letsconnect.extrasUtils.ExtraUtils

class InviteFragment : Fragment() {

    private lateinit var binding: FragmentInviteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invite, container, false)
        binding.shareBTN.setOnClickListener {
            var message = binding.messageET.text.toString()
            if(TextUtils.isEmpty(message)){
                Toast.makeText(activity,"Write some text",Toast.LENGTH_LONG).show()
            }
            else{
                val i = Intent(Intent.ACTION_SEND)
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, ExtraUtils.SHARING_URL);
                message+="\n Link: "
                message+= getString(R.string.link)
                i.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(i, ExtraUtils.SHARE_URL));
            }
        }


        return binding.root
    }

}
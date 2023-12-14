package com.harshit.letsconnect

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class LogoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = HomeFragment()
         AlertDialog.Builder(activity).setTitle("LOG OUT")
            .setIcon(com.google.android.gms.base.R.drawable.common_full_open_on_phone)
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes"){ _, _ ->
                Toast.makeText(context,"You are logging out",Toast.LENGTH_LONG).show()
                val intent = Intent(context, IntroductionActivity::class.java)
                startActivity(intent)
                FirebaseAuth.getInstance().signOut()
            }
            .setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
                findNavController().popBackStack()
            }.create().show()
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }

}
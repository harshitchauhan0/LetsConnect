package com.harshit.letsconnect.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.harshit.letsconnect.IntroductionActivity
import com.harshit.letsconnect.R
import com.harshit.letsconnect.extrasUtils.ExtraUtils


class LogoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val sp: SharedPreferences? = context?.getSharedPreferences(ExtraUtils.LOGIN, Context.MODE_PRIVATE)
         AlertDialog.Builder(activity).setTitle(ExtraUtils.LOG_OUT)
            .setIcon(com.google.android.gms.base.R.drawable.common_full_open_on_phone)
            .setMessage(ExtraUtils.LOG_OUT_MSG)
            .setPositiveButton("Yes"){ _, _ ->
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(context,"You are logged out",Toast.LENGTH_LONG).show()
                        sp!!.edit().putBoolean(ExtraUtils.LOGGED, false).apply()
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(context, IntroductionActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                }
            }
            .setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
                findNavController().popBackStack()
            }.create().show()
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }

}
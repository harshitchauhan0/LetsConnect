package com.harshit.letsconnect

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.harshit.letsconnect.extrasUtils.ExtraUtils
import com.harshit.letsconnect.models.UserModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var database: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navigationView:NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences(ExtraUtils.LOGIN, MODE_PRIVATE)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        appBarConfiguration = AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_profile, R.id.nav_group, R.id.nav_random, R.id.nav_log_out, R.id.nav_invite)
            .setDrawerLayout(drawer)
            .build()
        val navController = findNavController(this, R.id.nav_host_fragment)
        setupActionBarWithNavController(this, navController, appBarConfiguration)
        setupWithNavController(navigationView, navController)

        setUpAppBar()
        getFCMToken()
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isSuccessful){
                val token = it.result
//                Log.v("TAG",token)
                database.collection(ExtraUtils.USERS).document(auth.currentUser!!.uid).update(ExtraUtils.TOKEN,token).addOnCompleteListener {task->
                    if(task.isSuccessful){
                        Log.v("TAG","done")
                    }
                    else{
                        Log.v("TAG",task.exception.toString())
                    }
                }
            }
        }
    }

    private fun setUpAppBar() {
        val headerView = navigationView.getHeaderView(0)
        val headerName = headerView.findViewById<TextView>(R.id.nav_header_name)
        val headerEmail = headerView.findViewById<TextView>(R.id.nav_header_email)
        val headerImg = headerView.findViewById<CircleImageView>(R.id.nav_header_img)
        var imageUri: Uri? = null
        val storageReference = FirebaseStorage.getInstance().reference.child(ExtraUtils.PROFILE).child(auth.uid!!)
        CoroutineScope(Dispatchers.Default).launch {
            storageReference.downloadUrl.addOnCompleteListener {
                if(it.isSuccessful){
                    imageUri = it.result
                }
            }
        }
        database.collection(ExtraUtils.USERS).document(auth.uid!!).get().addOnCompleteListener {
            if(it.isSuccessful){
                if(it.result.toObject(UserModel::class.java)!=null){
                    val userModel = it.result.toObject(UserModel::class.java)!!
                    headerName.text = userModel.getUsername()
                    headerEmail.text = userModel.getPhoneNumber()
                    if(imageUri!=null){
                        Glide.with(this).load(imageUri).into(headerImg)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment)
        return (navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())
    }
}
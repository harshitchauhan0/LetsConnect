package com.harshit.letsconnect

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
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
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var database: FirebaseFirestore
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        appBarConfiguration = AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_profile, R.id.nav_group, R.id.nav_random, R.id.nav_log_out, R.id.nav_invite)
            .setDrawerLayout(drawer)
            .build()
        val navController = findNavController(this, R.id.nav_host_fragment)
        setupActionBarWithNavController(this, navController, appBarConfiguration)
        setupWithNavController(navigationView, navController)

        val headerView = navigationView.getHeaderView(0)
        val headerName = headerView.findViewById<TextView>(R.id.nav_header_name)
        val headerEmail = headerView.findViewById<TextView>(R.id.nav_header_email)
        val headerImg = headerView.findViewById<CircleImageView>(R.id.nav_header_img)
        var imageUri: Uri? = null
        val storageReference = FirebaseStorage.getInstance().reference.child("profile").child(auth.uid!!)
        CoroutineScope(Dispatchers.Default).launch {
            storageReference.downloadUrl.addOnCompleteListener {
                if(it.isSuccessful){
                    imageUri = it.result
                }
            }
        }
        database.collection("users").document(auth.uid!!).get().addOnCompleteListener {
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
package com.harshit.letsconnect

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var database: FirebaseDatabase
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

//        TODO("Tomorrow")
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


//        appBarConfiguration  = Builder(
//            R.id.nav_home,
//            R.id.nav_category,
//            R.id.nav_profile,
//            R.id.nav_new_products,
//            R.id.nav_my_orders,
//            R.id.nav_my_carts
//        )
//            .setDrawerLayout(drawer)
//            .build()

    }
}
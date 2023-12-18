package com.harshit.letsconnect

import com.google.firebase.auth.FirebaseAuth



object FirebaseUtils {
    fun currentUserId(): String? {
        return FirebaseAuth.getInstance().uid
    }
}

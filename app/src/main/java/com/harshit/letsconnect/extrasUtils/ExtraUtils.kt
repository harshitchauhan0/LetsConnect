package com.harshit.letsconnect.extrasUtils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


object ExtraUtils {
    fun currentUserId(): String? {
        return FirebaseAuth.getInstance().uid
    }

    fun getOtherUserFromChatroom(userIds: List<String>): DocumentReference {
        return if (userIds[0] == currentUserId()) {
            FirebaseFirestore.getInstance().collection("users").document(userIds[1])
        } else {
            FirebaseFirestore.getInstance().collection("users").document(userIds[0])
        }
    }

    fun getOtherImage(userModel:String ): StorageReference{
        return FirebaseStorage.getInstance().reference.child("profile")
            .child(userModel)
    }


}

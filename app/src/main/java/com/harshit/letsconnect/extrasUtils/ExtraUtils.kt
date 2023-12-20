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
            FirebaseFirestore.getInstance().collection(USERS).document(userIds[1])
        } else {
            FirebaseFirestore.getInstance().collection(USERS).document(userIds[0])
        }
    }

    fun getOtherImage(userModel:String ): StorageReference{
        return FirebaseStorage.getInstance().reference.child(PROFILE)
            .child(userModel)
    }


    const val SENT_BY_ME = "me"
    const val SENT_BY_BOT = "bot"
    const val NAME = "name"
    const val UID = "uid"
    const val PHONE = "phone"
    const val TIME = "time"
    const val TOKEN = "token"
    const val CHATROOM = "chatroom"
    const val USER_ID = "userIds"
    const val LAST_MESSAGE_TIMESTAMP = "lastMessageTimestamp"
    const val SHARING_URL = "Sharing URL"
    const val SHARE_URL = "Share URL"
    const val LOGIN = "login"
    const val LOG_OUT = "LOG OUT"
    const val LOG_OUT_MSG = "Are you sure you want to log out?"
    const val LOGGED = "logged"
    const val USERS = "users"
    const val PROFILE = "profile"
    const val LINK_CHAT_GPT = "https://api.openai.com/v1/completions"
    const val API_KEY = "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"
    const val CHATS = "chats"
    const val USERID = "userId"
    const val USERNAME = "username"
    const val URL_NOTIFICATION= "https://fcm.googleapis.com/fcm/send"
    const val NOTIFICATION_API_KEY = "AAAAxFdJ49w:APA91bHC_FfuqTM6fTXzr4xLLtbg8a6HCMkmtudKD180aF5AyJDzSY9EY5pCqdvku6hbB513KJFxaJRBxNzOSEZk2Z7MJ35AciD40QIivulaVqtdrbf0rn-MAaD8WNoAkcijqLZQaJ2S"
}

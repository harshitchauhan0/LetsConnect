package com.harshit.letsconnect.models

import com.google.firebase.Timestamp

data class MessageModel(
    var message: String = "",
    var senderId: String = "",
    var timestamp: Timestamp? = null
)
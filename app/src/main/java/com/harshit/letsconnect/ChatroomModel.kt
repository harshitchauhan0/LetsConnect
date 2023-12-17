package com.harshit.letsconnect

import com.google.firebase.Timestamp

data class ChatroomModel(
    var chatroomId: String = "",
    var userIds: List<String> = mutableListOf(),
    var lastMessageTimestamp: Timestamp? = null,
    var lastMessageSenderId: String = "",
    var lastMessage: String = ""
)
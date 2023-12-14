package com.harshit.letsconnect

import com.google.firebase.Timestamp

class UserModel {
    private var phoneNumber: String = ""
    private var username: String = ""
    private lateinit var timestamp: Timestamp

    // Empty constructor
    constructor()

    // Constructor with all attributes
    constructor(phoneNumber: String, username: String, timestamp: Timestamp) {
        this.phoneNumber = phoneNumber
        this.username = username
        this.timestamp = timestamp
    }

    // Getters and setters
    fun getPhoneNumber(): String {
        return phoneNumber
    }

    fun setPhoneNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    fun getUsername(): String {
        return username
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun getTimestamp(): Timestamp {
        return timestamp
    }

    fun setTimestamp(timestamp: Timestamp) {
        this.timestamp = timestamp
    }


}

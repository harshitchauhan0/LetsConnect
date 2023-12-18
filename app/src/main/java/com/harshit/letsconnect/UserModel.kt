package com.harshit.letsconnect

import com.google.firebase.Timestamp
import java.io.Serializable

class UserModel {
    private var phoneNumber: String = ""
    private var username: String = ""
    private lateinit var timestamp: Timestamp
    private var userId:String = ""

    // Empty constructor
    constructor()

    // Constructor with all attributes
    constructor(phoneNumber: String, username: String, timestamp: Timestamp, userId: String) {
        this.phoneNumber = phoneNumber
        this.username = username
        this.timestamp = timestamp
        this.userId = userId
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

    fun getUserId(): String {
        return userId
    }

    fun setUserId(userId: String) {
        this.userId = userId
    }

    override fun toString(): String {
        return "UserModel(phoneNumber='$phoneNumber', username='$username', timestamp=$timestamp, userId='$userId')"
    }
}

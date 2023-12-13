package com.harshit.letsconnect

import com.google.firebase.Timestamp

class UserModel {
    private var name: String = ""
    private var username: String = ""
    private lateinit var timestamp: Timestamp

    // Empty constructor
    constructor()

    // Constructor with all attributes
    constructor(name: String, username: String, timestamp: Timestamp) {
        this.name = name
        this.username = username
        this.timestamp = timestamp
    }

    // Getters and setters
    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
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

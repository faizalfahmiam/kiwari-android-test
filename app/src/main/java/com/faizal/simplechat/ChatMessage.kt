package com.faizal.simplechat

import java.util.Date

class ChatMessage {

    var messageText: String? = null
    var messageUser: String? = null
    var messageTime: Long = 0

    constructor(messageText: String, messageUser: String) {
        this.messageText = messageText
        this.messageUser = messageUser

        // Initialize to current time
        messageTime = Date().time
    }

    constructor() {

    }
}
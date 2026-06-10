package com.example.careathome


data class Message(
    val text: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
{
    // Convenience method
    fun isSentByMe(currentUserId: String): Boolean {
        return senderId == currentUserId
    }
}

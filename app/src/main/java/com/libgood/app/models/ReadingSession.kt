package com.libgood.app.models

import com.google.firebase.Timestamp

// Class for all reading sessions
data class ReadingSession(
    val id: String = "",
    val userId: String = "",
    val bookId: String = "",
    val bookTitle: String = "",
    val pagesRead: Int = 0,
    val xpEarned: Int = 0,
    val date: Timestamp? = null
)

package com.libgood.app.models

import com.google.firebase.Timestamp

// Class built for the books being read
data class Book(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val author: String = "",
    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val addedAt: Timestamp? = null
) {
    val isFinished: Boolean
        get() = totalPages > 0 && currentPage >= totalPages

    val progressPercent: Int
        get() = if (totalPages <= 0) 0 else ((currentPage.toFloat() / totalPages) * 100).toInt().coerceIn(0, 100)
}

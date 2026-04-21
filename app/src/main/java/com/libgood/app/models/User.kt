package com.libgood.app.models

import com.google.firebase.Timestamp

// Create the user class with all the valus relating to it
data class User(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val xp: Int = 0,
    val streak: Int = 0,
    val lastReadDate: Timestamp? = null,
    val createdAt: Timestamp? = null
) {
    /** Level derived from XP: every 100 XP = 1 level. */
    val level: Int
        get() = (xp / 100) + 1

    /** XP progress within the current level, 0..99. */
    val xpInCurrentLevel: Int
        get() = xp % 100
}

package com.libgood.app.models

import com.google.firebase.Timestamp

// Data class for achievements when they are earnt
data class Achievement(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val earnedAt: Timestamp? = null
) {
    /** Resolve the type string back to a typed enum, or null if unknown. */
    val achievementType: AchievementType?
        get() = AchievementType.values().firstOrNull { it.key == type }
}

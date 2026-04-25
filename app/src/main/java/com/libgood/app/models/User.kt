package com.libgood.app.models

import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.Date
import com.google.firebase.firestore.Exclude

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
    @get:Exclude
    val level: Int                     // Skips the getter when converting into firebase data types
        get() = (xp / 100) + 1

    @get:Exclude
    val xpInCurrentLevel: Int
        get() = xp % 100



    val currentStreak: Int
        @com.google.firebase.firestore.Exclude
        get() {
            val last = lastReadDate?.toDate() ?: return 0
            val now = Date()
            val cal1 = Calendar.getInstance().apply { time = last }
            val cal2 = Calendar.getInstance().apply { time = now }

            val sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)            // Correctly displays the current streak value on the dashboard
            if (sameDay) return streak

            // Was the last read yesterday?
            cal1.add(Calendar.DAY_OF_YEAR, 1)
            val wasYesterday = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)

            return if (wasYesterday) streak else 0
        }
}

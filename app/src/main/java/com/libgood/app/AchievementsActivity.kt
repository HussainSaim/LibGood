package com.libgood.app

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.libgood.app.models.AchievementType

// Function to display the achivements for the user
// Done in Kotlin because the list in dynamic
class AchievementsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        val container = findViewById<LinearLayout>(R.id.achievementsContainer)
        val emptyText = findViewById<TextView>(R.id.emptyText)

        FirebaseManager.getUserAchievements { unlocked ->
            val unlockedKeys = unlocked.mapNotNull { it.achievementType?.key }.toSet()

            container.removeAllViews()
            if (AchievementType.values().isEmpty()) {
                emptyText.visibility = View.VISIBLE
                return@getUserAchievements
            }
            emptyText.visibility = View.GONE

            // Show all achievements; visually distinguish unlocked vs locked.
            for (type in AchievementType.values()) {
                val card = TextView(this).apply {
                    val isUnlocked = unlockedKeys.contains(type.key)
                    text = buildString {
                        append(if (isUnlocked) "✅ " else "🔒 ")
                        append(type.title)
                        append("\n")
                        append(type.description)
                    }
                    textSize = 16f
                    setPadding(32, 24, 32, 24)
                    setTextColor(
                        ContextCompat.getColor(
                            this@AchievementsActivity,
                            if (isUnlocked) R.color.textPrimary else R.color.textSecondary
                        )
                    )
                    setBackgroundResource(R.drawable.achievement_card_bg)
                }
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 24)
                card.layoutParams = params
                container.addView(card)
            }
        }
    }
}

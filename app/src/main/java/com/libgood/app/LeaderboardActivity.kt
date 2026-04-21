package com.libgood.app

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

// Leaderboard based on all users current XP, the user is highlighted
class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val container = findViewById<LinearLayout>(R.id.leaderboardContainer)
        val emptyText = findViewById<TextView>(R.id.emptyText)
        val loadingText = findViewById<TextView>(R.id.loadingText)

        FirebaseManager.getLeaderboard { users ->
            loadingText.visibility = View.GONE

            if (users.isEmpty()) {
                emptyText.visibility = View.VISIBLE
                return@getLeaderboard
            }

            emptyText.visibility = View.GONE
            container.removeAllViews()

            val currentUid = FirebaseManager.currentUserId

            for ((index, user) in users.withIndex()) {
                val rank = index + 1
                val isCurrentUser = user.uid == currentUid

                val row = TextView(this).apply {
                    text = buildString {
                        // Rank medal for top 3
                        when (rank) {
                            1 -> append("🥇 ")                   // The top three are given medals before their name
                            2 -> append("🥈 ")
                            3 -> append("🥉 ")
                            else -> append("#$rank  ")
                        }
                        append(user.username.ifBlank { "Anonymous" })
                        append("  —  Level ${user.level}  •  ${user.xp} XP")
                        if (user.streak > 0) append("  •  🔥${user.streak}")
                    }
                    textSize = 16f
                    setPadding(32, 28, 32, 28)

                    if (isCurrentUser) {
                        setBackgroundColor(ContextCompat.getColor(this@LeaderboardActivity, R.color.primary))
                        setTextColor(0xFFFFFFFF.toInt())
                        setTypeface(null, Typeface.BOLD)
                    } else {
                        setBackgroundResource(R.drawable.achievement_card_bg)
                        setTextColor(
                            ContextCompat.getColor(
                                this@LeaderboardActivity,
                                R.color.textPrimary
                            )
                        )
                    }
                }

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 16)
                row.layoutParams = params
                container.addView(row)
            }
        }
    }
}

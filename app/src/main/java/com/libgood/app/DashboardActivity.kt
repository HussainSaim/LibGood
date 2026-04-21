package com.libgood.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var levelText: TextView
    private lateinit var xpText: TextView    // lateinit ensures that the program runs even if these are null with the implication that theywill be values later
    private lateinit var streakText: TextView
    private lateinit var xpBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Go to LoginActivity if the user is not logged in
        if (!FirebaseManager.isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        welcomeText = findViewById(R.id.welcomeText)
        levelText = findViewById(R.id.levelText)
        xpText = findViewById(R.id.xpText)
        streakText = findViewById(R.id.streakText)
        xpBar = findViewById(R.id.xpProgressBar)

        findViewById<Button>(R.id.logReadingButton).setOnClickListener {
            startActivity(Intent(this, LogReadingActivity::class.java))
        }
        findViewById<Button>(R.id.addBookButton).setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }
        findViewById<Button>(R.id.achievementsButton).setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
        }
        findViewById<Button>(R.id.leaderboardButton).setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            FirebaseManager.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // Refreshes the stats every time the user comes back from another screen
    override fun onResume() {
        super.onResume()

        refresh()
    }

    // Creates the dashboard using the information gained from Firebase
    private fun refresh() {
        FirebaseManager.getCurrentUser { user ->
            if (user == null) return@getCurrentUser
            welcomeText.text = getString(R.string.welcome_format, user.username.ifBlank { "Reader" })
            levelText.text = getString(R.string.level_format, user.level)
            xpText.text = getString(R.string.xp_format, user.xpInCurrentLevel, 100, user.xp)
            streakText.text = getString(R.string.streak_format, user.streak)
            xpBar.progress = user.xpInCurrentLevel
        }
    }
}

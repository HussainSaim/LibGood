package com.libgood.app

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.libgood.app.models.Book


class LogReadingActivity : AppCompatActivity() {

    private lateinit var bookSpinner: Spinner           // Spinners are dropdown menus
    private lateinit var pagesField: EditText
    private lateinit var submitButton: Button
    private lateinit var emptyText: TextView

    private var books: List<Book> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_reading)

        bookSpinner = findViewById(R.id.bookSpinner)
        pagesField = findViewById(R.id.pagesField)
        submitButton = findViewById(R.id.submitButton)
        emptyText = findViewById(R.id.emptyText)

        loadBooks()

        submitButton.setOnClickListener { submit() }
    }

    // Gets books from thje database or prompt them to and deny submitting
    private fun loadBooks() {
        FirebaseManager.getUserBooks { result ->
            books = result
            if (books.isEmpty()) {
                emptyText.visibility = View.VISIBLE
                bookSpinner.visibility = View.GONE
                submitButton.isEnabled = false
                emptyText.text = getString(R.string.no_books_message)
            } else {
                emptyText.visibility = View.GONE
                bookSpinner.visibility = View.VISIBLE
                submitButton.isEnabled = true
                val labels = books.map { "${it.title}, by ${it.author}" }
                bookSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
            }
        }
    }

    // Function run when user submits
    private fun submit() {

        // Error Handling
        if (books.isEmpty()) return
        val pages = pagesField.text.toString().toIntOrNull() ?: 0
        if (pages <= 0) {
            toast("Enter how many pages you read")
            return
        }
        val selectedBook = books[bookSpinner.selectedItemPosition]
        submitButton.isEnabled = false

        FirebaseManager.logReadingSession(selectedBook, pages) { user, newAchievements, error ->
            submitButton.isEnabled = true
            if (error != null || user == null) {
                toast(error ?: "Could not log session")
                return@logReadingSession
            }

            val xpEarned = pages * FirebaseManager.XP_PER_PAGE
            val message = buildString {
                append(getString(R.string.session_logged_format, pages, xpEarned))
                append("\n")
                append(getString(R.string.current_level_format, user.level, user.xp))
                append("\n")
                append(getString(R.string.current_streak_format, user.streak))
                if (newAchievements.isNotEmpty()) {
                    append("\n\n🏆 ")
                    append(getString(R.string.unlocked_label))
                    append("\n")
                    newAchievements.forEach { append("• ${it.title}\n") }
                }
            }

            AlertDialog.Builder(this)
                .setTitle(R.string.session_logged_title)
                .setMessage(message)
                .setPositiveButton(R.string.ok) { _, _ -> finish() }
                .show()
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

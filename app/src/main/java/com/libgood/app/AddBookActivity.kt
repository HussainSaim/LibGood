package com.libgood.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// Allows for users to input books of their choosing
class AddBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        val titleField = findViewById<EditText>(R.id.titleField)
        val authorField = findViewById<EditText>(R.id.authorField)
        val pagesField = findViewById<EditText>(R.id.pagesField)
        val saveButton = findViewById<Button>(R.id.saveBookButton)

        saveButton.setOnClickListener {
            val title = titleField.text.toString().trim()
            val author = authorField.text.toString().trim()
            val pages = pagesField.text.toString().toIntOrNull() ?: 0

            if (title.isEmpty() || author.isEmpty() || pages <= 0) {
                toast("Please fill in title, author and a valid page count")
                return@setOnClickListener
            }

            saveButton.isEnabled = false
            FirebaseManager.addBook(title, author, pages) { success, error ->
                saveButton.isEnabled = true
                if (success) {
                    toast("Book added!")
                    finish()
                } else {
                    toast(error ?: "Could not save book")
                }
            }
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

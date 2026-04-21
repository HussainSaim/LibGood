package com.libgood.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// Near identical to LoginActivity but with a username section and stronger validation
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val usernameField = findViewById<EditText>(R.id.usernameField)
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                toast("Email and password are required")
                return@setOnClickListener
            }
            if (password.length < 6) {
                toast("Password must be at least 6 characters")
                return@setOnClickListener
            }

            registerButton.isEnabled = false
            FirebaseManager.register(email, password, username) { success, error ->
                registerButton.isEnabled = true
                if (success) {
                    toast("Account created — welcome!")
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                } else {
                    toast(error ?: "Registration failed")
                }
            }
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

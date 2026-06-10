package com.example.careathome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpText: TextView
    private lateinit var forgetPasswordText: TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login2)

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signUpText = findViewById(R.id.signUpRedirectText)
        forgetPasswordText = findViewById(R.id.forgotPasswordText)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Login Button Click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        // Sign Up Navigation
        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        // Forgot Password Click
        forgetPasswordText.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            showForgotPasswordDialog(email)
        }
    }

    private fun loginUser(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Home::class.java))
                    finish()
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Unknown error"
                    Log.e("LoginError", "Login failed", task.exception)
                    Toast.makeText(this, "Login Failed: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun showForgotPasswordDialog(prefilledEmail: String = "") {
        val dialogView = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        val emailInput = dialogView.findViewById<EditText>(R.id.emailEditText)
        emailInput.setText(prefilledEmail)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<TextView>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        Log.d("ResetPassword", "Trying to send email to $email")
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ResetPassword", "Email sent successfully")
                    Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.e("ResetPassword", "Failed to send email", task.exception)
                    Toast.makeText(this, "Failed: ${task.exception?.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}

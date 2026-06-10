package com.example.careathome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.careathome.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var countryDropdown: AutoCompleteTextView
    private lateinit var signUpButton: Button
    private lateinit var loginRedirectText: TextView
    private lateinit var isDoctorCheckbox: CheckBox
    private lateinit var doctorFieldsLayout: LinearLayout
    private lateinit var specialtyEditText: TextInputEditText
    private lateinit var locationEditText: TextInputEditText
    private lateinit var qualificationsEditText: TextInputEditText
    private lateinit var experienceEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth
        database = Firebase.database.reference

        nameEditText = findViewById(R.id.signup_name)
        emailEditText = findViewById(R.id.signup_email)
        passwordEditText = findViewById(R.id.signup_password)
        confirmPasswordEditText = findViewById(R.id.signup_confirm_password)
        countryDropdown = findViewById(R.id.signup_country_dropdown)
        signUpButton = findViewById(R.id.signup_button)
        loginRedirectText = findViewById(R.id.loginRedirectText)
        isDoctorCheckbox = findViewById(R.id.isDoctorCheckbox)
        doctorFieldsLayout = findViewById(R.id.doctorFieldsLayout)
        specialtyEditText = findViewById(R.id.signup_specialty)
        locationEditText = findViewById(R.id.signup_location)
        qualificationsEditText = findViewById(R.id.signup_qualifications)
        experienceEditText = findViewById(R.id.signup_experience)
        phoneEditText = findViewById(R.id.signup_phone)

        setupCountryDropdown()

        isDoctorCheckbox.setOnCheckedChangeListener { _, isChecked ->
            doctorFieldsLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val country = countryDropdown.text.toString().trim()
            val isDoctor = isDoctorCheckbox.isChecked

            if (validateInputs(name, email, password, confirmPassword, country, isDoctor)) {
                registerUser(name, email, password, country, isDoctor)
            }
        }

        loginRedirectText.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun setupCountryDropdown() {
        val countries = resources.getStringArray(R.array.countries_array)
        val adapter = ArrayAdapter(this, R.layout.dropdown_menu_item, countries)
        countryDropdown.setAdapter(adapter)
    }

    private fun validateInputs(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        country: String,
        isDoctor: Boolean
    ): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return false
        }


        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        if (isDoctor) {
            val specialty = specialtyEditText.text.toString().trim()
            val location = locationEditText.text.toString().trim()
            val qualifications = qualificationsEditText.text.toString().trim()
            val experience = experienceEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
                return false
            }

            if (specialty.isEmpty() || location.isEmpty() || qualifications.isEmpty() || experience.isEmpty()) {
                Toast.makeText(this, "Please fill all doctor fields", Toast.LENGTH_SHORT).show()
                return false
            }

            if (experience.toIntOrNull() == null) {
                Toast.makeText(this, "Enter valid years of experience", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true
    }

    private fun registerUser(
        name: String,
        email: String,
        password: String,
        country: String,
        isDoctor: Boolean
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val user = FirebaseAuth.getInstance().currentUser

                    user?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val x:Int
                                } else {
                                    Log.d("Firebase", "Profile update failed: ${task.exception?.message}")
                                }
                            }
                    }
                    if (isDoctor) {
                        val doctor = Doctor(
                            id = userId,
                            name = name,
                            email = email,
                            country = country,
                            specialty = specialtyEditText.text.toString().trim(),
                            location = locationEditText.text.toString().trim(),
                            qualifications = qualificationsEditText.text.toString().trim(),
                            yearsOfExperience = experienceEditText.text.toString().trim().toInt(),
                            phoneNumber = phoneEditText.text.toString().trim()
                        )

                        database.child("Doctor").child(userId).setValue(doctor)
                            .addOnSuccessListener {
                                proceedToDoctorHome()
                            }
                            .addOnFailureListener { e ->
                                showError("Failed to save doctor data: ${e.message}")
                            }
                    } else {
                        val user = Users(
                            id = userId,
                            name = name,
                            email = email,
                            country = country
                        )

                        database.child("Users").child(userId).setValue(user)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "User registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this, Home::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                showError("Failed to save user data: ${e.message}")
                            }
                    }
                } else {
                    showError("Registration failed: ${task.exception?.message}")
                }
            }
    }

    private fun showError(message: String?) {
        Toast.makeText(this, message ?: "Unknown error", Toast.LENGTH_SHORT).show()
    }

    private fun proceedToDoctorHome() {
        Toast.makeText(this, "Doctor registered successfully", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, Home::class.java))
        finish()
    }
}

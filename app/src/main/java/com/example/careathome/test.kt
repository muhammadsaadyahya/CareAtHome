package com.example.careathome

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class test : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FirestoreTest" // Use a constant for your log tag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)


        val user = FirebaseAuth.getInstance().currentUser

        user?.let {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("Saad Yahya")
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

        // Run your Firestore queries here
       // runQueries()
    }

}

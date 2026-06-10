package com.example.careathome

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class Profile : Fragment() {
    private lateinit var profileImageView: ShapeableImageView
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_profile, container, false)



        val user = FirebaseAuth.getInstance().currentUser

        val name:String



        view.findViewById<TextView>(R.id.userName).text=user!!.displayName.toString()
        view.findViewById<TextView>(R.id.userEmail).text=user!!.email.toString()

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val selectedImageUri = result.data!!.data
                profileImageView.setImageURI(selectedImageUri)
            }
        }

        view.findViewById<ImageView>(R.id.editProfileIcon).setOnClickListener {
            profileImageView = view.findViewById(R.id.profileImage)



            view.findViewById<ImageView>(R.id.editProfileIcon).setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                imagePickerLauncher.launch(intent)
            }


        }



        view.findViewById<LinearLayout>(R.id.about).setOnClickListener {

            val aboutText = """
    <b>About Care at Home</b><br><br>
    Care at Home is your trusted companion for managing health and wellness services from the comfort of your home. Whether you need a qualified nurse, physical therapy, elder care, or post-surgery support, we bring compassionate and professional care right to your doorstep.<br><br>
    Our mission is to make healthcare more accessible, personalized, and convenient for everyone. With an easy-to-use interface, real-time service booking, and verified caregivers, Care at Home ensures you and your loved ones receive the attention you deserve—without the stress of traveling.
    """.trimIndent()
            val dialog = AlertDialog.Builder(requireContext())
                .setMessage(Html.fromHtml(aboutText, Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton("OK", null)
                .create()

            dialog.show()

            dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.login_bg_gradient))


        }
        view.findViewById<LinearLayout>(R.id.privacy).setOnClickListener{
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_privacy_policy, null)
            val privacyTextView = dialogView.findViewById<TextView>(R.id.privacyText)

            val fullText = """
    <b>Privacy Policy – Care at Home</b>
    <br><b>Effective Date:</b> [Insert Date]<br><br>

    At Care at Home, your privacy is our top priority. This Privacy Policy outlines how we collect, use, 
    and protect your personal information when you use our mobile application and related services.

    <br><b>📌 1. Information We Collect</b><br>
    • Personal Information:<br> Name, phone number, email address, and address (provided during booking or profile setup).
    • Health-related Information:<br> Details related to service requirements or medical needs (only when necessary).
    • Location Data:<br> With your permission, we may access your location to connect you with nearby caregivers.
    • Usage Data:<br> App usage statistics, interaction logs, and technical data to improve performance.

    <br><b>🔐 2. How We Use Your Information</b><br>
    We use your data to:<br>
    • Provide and manage healthcare services
    • Match you with the right caregivers
    • Communicate with you about bookings and updates
    • Enhance app performance and user experience
    • Comply with legal obligations

    <br><b>🤝 3. Sharing Your Information</b><br>
    We do not sell your personal data. We may share limited information with:
    • Verified service providers (e.g., caregivers)
    • Third-party tools for app functionality (e.g., maps, analytics)
    • Authorities, only if legally required

    <br><b>🛡️ 4. Your Rights</b><br>
    You can:<br>
    • View or update your profile information
    • Request deletion of your account and data
    • Control app permissions like location or notifications

    <br><b>🔒 5. Data Security</b><br>
    We use secure servers, encrypted connections, and access controls to protect your data. However, no system 
    is 100% secure, so we encourage safe usage practices.

    <br><b>📞 6. Contact Us</b><br>
    If you have questions or concerns, please contact us at:<br><br>
    Email: support@careathome.com<br>
    Phone: +1-234-567-890
""".trimIndent()



            privacyTextView.text = Html.fromHtml(fullText, Html.FROM_HTML_MODE_LEGACY)

            AlertDialog.Builder(requireContext())
                .setTitle("Privacy Policy")
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .show()
        }
        view.findViewById<LinearLayout>(R.id.terms).setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dailog_terms, null)
            val TermsTextView = dialogView.findViewById<TextView>(R.id.termsText)
            val termsOfService = """
    <b>Terms and Services – Care at Home</b>
    <br><b>Effective Date:</b> [Insert Date]<br><br>

    Welcome to Care at Home! By using our mobile application ("the App") and the services we provide, you agree to the following terms and conditions. Please read them carefully.

    <br><b>1. Agreement to Terms</b><br>
    By accessing or using the Care at Home App, you agree to comply with and be bound by these Terms and Services, as well as our Privacy Policy. If you do not agree with these terms, you should not use the App.

    <br><b>2. Services Provided</b><br>
    Care at Home provides healthcare services including nursing, physical therapy, elder care, and post-surgery support at home. These services are available through the App for booking, scheduling, and communication with qualified caregivers.

    <br><b>3. Eligibility</b><br>
    You must be at least 18 years old to use the App and access the services. If you are under 18, you may only use the App with the involvement and consent of a parent or guardian.

    <br><b>4. Account Registration</b><br>
    To use certain features of the App, you may need to create an account. You are responsible for maintaining the confidentiality of your account credentials and for all activities that occur under your account. Please notify us immediately if you believe your account has been compromised.

    <br><b>5. Booking and Service Fees</b><br>
    All service bookings through the App are subject to availability and may incur service fees. You agree to pay the fees associated with any services you book, as specified in the App.

    <br><b>6. User Responsibilities</b><br>
    You agree to use the App and services in accordance with all applicable laws and regulations. You shall not:<br>
    - Engage in any illegal or fraudulent activities
    - Violate the privacy or rights of other users or caregivers
    - Use the App for any purpose that could harm the functionality or integrity of the service

    <br><b>7. Service Availability</b><br>
    The availability of caregivers and services may vary based on location and demand. We do not guarantee that all services will be available at all times.

    <br><b>8. Payment and Cancellation</b><br>
    You agree to pay for all services booked through the App. If you need to cancel or reschedule a service, please refer to the cancellation policy outlined in the App. Failure to comply with the cancellation policy may result in a fee.

    <br><b>9. Limitation of Liability</b><br>
    Care at Home is not liable for any damages, injuries, or losses arising from the use of our services, except where required by law. We are not responsible for the actions of third-party service providers or caregivers.

    <br><b>10. Termination</b><br>
    We reserve the right to suspend or terminate your access to the App at any time, without notice, for any violation of these Terms and Services.

    <br><b>11. Changes to Terms</b><br>
    We may update or modify these Terms and Services at any time. Any changes will be posted on this page, and the "Effective Date" will be updated accordingly. Your continued use of the App constitutes acceptance of the updated terms.

    <br><b>12. Governing Law</b><br>
    These Terms and Services are governed by the laws of Article ABC. Any disputes arising from the use of the App or services will be resolved in the appropriate courts located in [Insert Location].

    <br><b>13. Contact Us</b><br>
    If you have any questions or concerns regarding these Terms and Services, please contact us at:
    <br><br>Email: support@careathome.com<br>
    Phone: +1-234-567-890
""".trimIndent()

            TermsTextView.text = Html.fromHtml(termsOfService, Html.FROM_HTML_MODE_LEGACY)

            AlertDialog.Builder(requireContext())
                .setTitle("Terms Of Service")
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .show()


        }

        fun isDoctor(callback: (Boolean) -> Unit) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                callback(false)
                return
            }

            val doctorRef = FirebaseDatabase.getInstance().getReference("Doctor").child(userId)
            doctorRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result.exists())
                } else {
                    callback(false)
                }
            }
        }



        fun showAlert(title: String, message: String) {
            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
        view.findViewById<LinearLayout>(R.id.UpdateProfile).setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_profile, null)

            val nameField = dialogView.findViewById<EditText>(R.id.editName)
            val phoneField = dialogView.findViewById<EditText>(R.id.editPhone)
            val descField = dialogView.findViewById<EditText>(R.id.editDescription)
            val countryField = dialogView.findViewById<EditText>(R.id.editCountry)
            val specialtyField = dialogView.findViewById<EditText>(R.id.editSpecialty)
            val locationField = dialogView.findViewById<EditText>(R.id.editLocation)
            val qualField = dialogView.findViewById<EditText>(R.id.editQualifications)
            val expField = dialogView.findViewById<EditText>(R.id.editYearsOfExperience)

            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid ?: return@setOnClickListener
            val dbRef = FirebaseDatabase.getInstance().getReference()

            // Try Doctor first
            dbRef.child("Doctor").child(userId).get().addOnSuccessListener { snapshot ->
                val data = snapshot.value as? Map<*, *> ?: emptyMap<Any, Any>()

                nameField.setText(data["name"]?.toString() ?: "")
                phoneField.setText(data["phone"]?.toString() ?: "")
                descField.setText(data["description"]?.toString() ?: "")
                countryField.setText(data["country"]?.toString() ?: "")
                specialtyField.setText(data["specialty"]?.toString() ?: "")
                locationField.setText(data["location"]?.toString() ?: "")
                qualField.setText(data["qualifications"]?.toString() ?: "")
                expField.setText(data["experience"]?.toString() ?: "")

                specialtyField.isEnabled = true
                locationField.isEnabled = true
                qualField.isEnabled = true
                expField.isEnabled = true

            }.addOnFailureListener {
                // Try Users node if Doctor fails
                dbRef.child("Users").child(userId).get().addOnSuccessListener { snapshot ->
                    val data = snapshot.value as? Map<*, *> ?: emptyMap<Any, Any>()

                    nameField.setText(data["name"]?.toString() ?: "")
                    phoneField.setText(data["phone"]?.toString() ?: "")
                    descField.setText(data["description"]?.toString() ?: "")
                    countryField.setText(data["country"]?.toString() ?: "")
                }
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Update Profile")
                .setView(dialogView)
                .setPositiveButton("Update") { _, _ ->
                    val updatedData = mapOf(
                        "name" to nameField.text.toString().trim(),
                        "phone" to phoneField.text.toString().trim(),
                        "description" to descField.text.toString().trim(),
                        "country" to countryField.text.toString().trim(),
                        "specialty" to specialtyField.text.toString().trim(),
                        "location" to locationField.text.toString().trim(),
                        "qualifications" to qualField.text.toString().trim(),
                        "experience" to expField.text.toString().trim()
                    )

                    // Try updating Doctor node first
                    dbRef.child("Doctor").child(userId).updateChildren(updatedData)
                        .addOnSuccessListener {
                            showAlert("Success", "Doctor profile updated.")
                        }
                        .addOnFailureListener {
                            // Try updating Users node
                            dbRef.child("Users").child(userId).updateChildren(updatedData)
                                .addOnSuccessListener {
                                    showAlert("Success", "User profile updated.")
                                }
                                .addOnFailureListener { e ->
                                    showAlert("Error", e.localizedMessage ?: "Failed to update profile.")
                                }
                        }

                }
                .setNegativeButton("Cancel", null)
                .show()
        }





        view.findViewById<LinearLayout>(R.id.passwd).setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null)

            val currentPassword:EditText = dialogView.findViewById(R.id.currentPassword)
            val newPassword:EditText = dialogView.findViewById(R.id.newPassword)
            val confirmPassword:EditText = dialogView.findViewById(R.id.confirmPassword)

            AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change") { _, _ ->
                    val current = currentPassword.text.toString()
                    val newPass = newPassword.text.toString()
                    val confirm = confirmPassword.text.toString()

                    val user = FirebaseAuth.getInstance().currentUser

                    if (user == null) {
                        showAlert("Error", "User not logged in.")
                        return@setPositiveButton
                    }

                    if (newPass != confirm) {
                        showAlert("Mismatch", "New passwords do not match.")
                        return@setPositiveButton
                    }

                    if (current.isEmpty() || newPass.isEmpty()) {
                        showAlert("Incomplete", "Please fill in all fields.")
                        return@setPositiveButton
                    }

                    val email = user.email
                    if (email != null) {
                        val credential = EmailAuthProvider.getCredential(email, current)

                        user.reauthenticate(credential).addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        showAlert("Success", "Password changed successfully.")
                                    } else {
                                        showAlert("Update Failed", updateTask.exception?.localizedMessage ?: "Unknown error")
                                    }
                                }
                            } else {
                                showAlert("Authentication Failed", authTask.exception?.localizedMessage ?: "Wrong current password")
                            }
                        }
                    } else {
                        showAlert("Error", "No email linked to this account.")
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        

        view.findViewById<LinearLayout>(R.id.LogOut).setOnClickListener {
            FirebaseAuth.getInstance().signOut()


            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }



        return view
    }


}
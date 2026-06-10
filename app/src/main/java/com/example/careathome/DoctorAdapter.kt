package com.example.careathome

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.careathome.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class DoctorAdapter(
    private val onDoctorClick: (Doctor) -> Unit,
    private val onBookClick: (Doctor) -> Unit,
    private val onMessageClick: (Doctor) -> Unit
) : ListAdapter<Doctor, DoctorAdapter.DoctorViewHolder>(DoctorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_care_giver, parent, false)
        return DoctorViewHolder(view, onDoctorClick, onBookClick, onMessageClick)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DoctorViewHolder(
        itemView: View,
        private val onDoctorClick: (Doctor) -> Unit,
        private val onBookClick: (Doctor) -> Unit,
        private val onMessageClick: (Doctor) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(doctor: Doctor) {
            val nameText = itemView.findViewById<TextView>(R.id.nameText)
            val specialtyText = itemView.findViewById<TextView>(R.id.specialtyText)
            val qualificationsText = itemView.findViewById<TextView>(R.id.qualificationsText)
            val experienceText = itemView.findViewById<TextView>(R.id.experienceText)
            val ratingText = itemView.findViewById<TextView>(R.id.ratingText)
            val countryText = itemView.findViewById<TextView>(R.id.countryText)
            val phoneText = itemView.findViewById<TextView>(R.id.phoneText)
            val emailText = itemView.findViewById<TextView>(R.id.emailText)
            val profileImage = itemView.findViewById<ShapeableImageView>(R.id.profileImage)
            val bookButton = itemView.findViewById<MaterialButton>(R.id.bookButton)
            val messageButton = itemView.findViewById<MaterialButton>(R.id.messageButton)
            val callButton = itemView.findViewById<MaterialButton>(R.id.callButton)

            nameText.text = doctor.name
            specialtyText.text = "${doctor.specialty} • ${doctor.location}"
            qualificationsText.text = doctor.qualifications
            experienceText.text = "${doctor.yearsOfExperience} years experience"
            ratingText.text = "⭐ ${"%.1f".format(doctor.rating)} (${doctor.reviewCount ?: 0} Reviews)"
            countryText.text = "From: ${doctor.country}"
            phoneText.text = "Phone: ${doctor.phoneNumber}"
            emailText.text = "Email: ${doctor.email}"
            profileImage.setImageResource(Doctor.DEFAULT_IMAGE_RES)

            itemView.setOnClickListener { onDoctorClick(doctor) }
            bookButton.setOnClickListener { onBookClick(doctor) }
            messageButton.setOnClickListener { onMessageClick(doctor) }
            callButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${doctor.phoneNumber}")
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    class DoctorDiffCallback : DiffUtil.ItemCallback<Doctor>() {
        override fun areItemsTheSame(oldItem: Doctor, newItem: Doctor): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Doctor, newItem: Doctor): Boolean {
            return oldItem == newItem
        }
    }
}
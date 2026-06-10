package com.example.careathome

import com.example.careathome.R

data class Doctor(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val country: String = "",
    val specialty: String = "",
    val location: String = "",
    val qualifications: String = "",
    val yearsOfExperience: Int = 0,
    val rating: Float = kotlin.random.Random.nextDouble(3.0, 5.0).toFloat(),
    val reviewCount: Int? = kotlin.random.Random.nextInt(1, 100)
) {
    companion object {
        val DEFAULT_IMAGE_RES = R.drawable.ic_person
    }
}
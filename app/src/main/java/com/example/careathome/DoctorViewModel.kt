package com.example.careathome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careathome.dDoctorViewModelata.DoctorRepository
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {
    private val repository = DoctorRepository()
    val doctors = repository.doctors

    fun addDoctor(
        name: String,
        email: String,
        phoneNumber: String,
        country: String,
        specialty: String,
        location: String,
        qualifications: String,
        yearsOfExperience: Int
    ) {
        viewModelScope.launch {
            repository.addDoctor(
                Doctor(
                    name = name,
                    email = email,
                    phoneNumber = phoneNumber,
                    country = country,
                    specialty = specialty,
                    location = location,
                    qualifications = qualifications,
                    yearsOfExperience = yearsOfExperience,
                    rating = kotlin.random.Random.nextDouble(3.0, 5.0).toFloat(),
                    reviewCount = kotlin.random.Random.nextInt(1, 100)
                )
            )
        }
    }

    fun updateDoctor(doctor: Doctor) {
        viewModelScope.launch {
            repository.updateDoctor(doctor)
        }
    }

    fun deleteDoctor(doctorId: String) {
        viewModelScope.launch {
            repository.deleteDoctor(doctorId)
        }
    }
}

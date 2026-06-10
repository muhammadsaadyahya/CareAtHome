package com.example.careathome.dDoctorViewModelata

import com.example.careathome.Doctor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DoctorRepository {
    private val database = FirebaseDatabase.getInstance()
    private val doctorsRef = database.getReference("Doctor")

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> = _doctors

    init {
        // Listen for realtime updates
        doctorsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val doctorsList = mutableListOf<Doctor>()
                for (doctorSnapshot in snapshot.children) {
                    doctorSnapshot.getValue(Doctor::class.java)?.let { doctor ->
                        if (true) {
                            doctorsList.add(doctor.copy(id = doctorSnapshot.key ?: ""))
                        }
                    }
                }
                _doctors.value = doctorsList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error (you might want to log this)
            }
        })
    }

    fun addDoctor(doctor: Doctor) {
        val newDoctorRef = doctorsRef.push()
        newDoctorRef.setValue(doctor.copy(id = newDoctorRef.key ?: ""))
    }

    fun updateDoctor(doctor: Doctor) {
        if (doctor.id!!.isNotEmpty()) {
            doctorsRef.child(doctor.id).setValue(doctor)
        }
    }

    fun deleteDoctor(doctorId: String) {
        if (doctorId.isNotEmpty()) {
            doctorsRef.child(doctorId).removeValue()
        }
    }
}
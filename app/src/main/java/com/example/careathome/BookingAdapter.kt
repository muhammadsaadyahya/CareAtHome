package com.example.careathome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookingAdapter(
    private val bookings: List<Booking>,
    private val isDoctor: Boolean
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {
    fun getUserNameById(uid: String, callback: (String) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("Users").child(uid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                callback(name ?: "Unknown")
            }

            override fun onCancelled(error: DatabaseError) {
                callback("Unknown")
            }
        })
    }

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.bookingName)
        val date: TextView = view.findViewById(R.id.bookingDate)
        val time: TextView = view.findViewById(R.id.bookingTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

        // Show name based on user type
        if (isDoctor) {
            getUserNameById(booking.userId) { name ->
                holder.name.text = "Patient: $name"
            }
        } else {
            holder.name.text = "Doctor: ${booking.doctorName ?: booking.doctorId}"
        }
        holder.date.text = "Date: ${booking.date}"
        holder.time.text = "Time: ${booking.time}"
    }

    override fun getItemCount(): Int = bookings.size
}

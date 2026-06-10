package com.example.careathome

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var userNameView: TextView
    private lateinit var scheduledAppointmentsBtn: Button
    private lateinit var careHistoryBtn: Button
    private lateinit var databaseRef: DatabaseReference

    private var isDoctor = false



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_home_fragment, container, false)
        userNameView = view.findViewById(R.id.userName)
        userNameView.text = user?.displayName ?: "Guest"

        scheduledAppointmentsBtn = view.findViewById(R.id.ScheduledAppointments)
        careHistoryBtn = view.findViewById(R.id.CareHistory)
        databaseRef = FirebaseDatabase.getInstance().getReference("Bookings")

        setUserRole()

        scheduledAppointmentsBtn.setOnClickListener {
            loadBookings("future") { bookings ->
                val futureBookings = filterFutureBookings(bookings)
                showBookingDialog("Your Future Appointments", futureBookings)
            }
        }

        careHistoryBtn.setOnClickListener {
            loadBookings("past") { bookings ->
                val pastBookings = filterPastBookings(bookings)
                showBookingDialog("Your Past Appointments", pastBookings)


            }
        }

        return view
    }
    private fun setUserRole() {
        // Reference to the Doctors table using the current user's UID
        val doctorRef = FirebaseDatabase.getInstance().getReference("Doctor").child(user!!.uid)

        // Check if the user exists in the Doctors table
        doctorRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // If a document exists with the user's UID, they are a doctor
                isDoctor = snapshot.exists()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
                isDoctor=false
            }
        })
    }


    private fun loadBookings(type: String, onResult: (List<Booking>) -> Unit) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Booking>()
                for (child in snapshot.children) {
                    val booking = child.getValue(Booking::class.java)

                    // If the user is a doctor, match with doctorId
                    val matchDoctor = isDoctor && booking?.doctorId == user!!.uid
                    // If the user is a patient, match with userId
                    val matchPatient = !isDoctor && booking?.userId == user!!.uid
                    if ((matchDoctor || matchPatient) && booking != null) {
                        list.add(booking)
                    }
                }
                onResult(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun filterFutureBookings(bookings: List<Booking>): List<Booking> {
        val currentDate = Date() // Current date and time
        return bookings.filter { booking ->
            val bookingDateTimeString = "${booking.date} ${booking.time}"
            val bookingDate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(bookingDateTimeString)
            bookingDate.after(currentDate) // Check if the booking is in the future
        }
    }

    private fun filterPastBookings(bookings: List<Booking>): List<Booking> {
        val currentDate = Date() // Current date and time
        return bookings.filter { booking ->
            val bookingDateTimeString = "${booking.date} ${booking.time}"
            val bookingDate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(bookingDateTimeString)
            bookingDate.before(currentDate) // Check if the booking is in the past
        }
    }

    private fun showBookingDialog(title: String, bookings: List<Booking>) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_recycler)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(R.drawable.login_bg_gradient)

        val titleView = dialog.findViewById<TextView>(R.id.dialogTitle)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewDialog)

        titleView.text = title
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = BookingAdapter(bookings,isDoctor)

        dialog.show()
    }
}

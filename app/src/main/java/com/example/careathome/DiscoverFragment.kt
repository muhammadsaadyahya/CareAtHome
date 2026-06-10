import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.careathome.Booking
import com.example.careathome.R
import com.example.careathome.Doctor
import com.example.careathome.DoctorAdapter
import com.example.careathome.PrivateChat
import com.example.careathome.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DiscoverFragment : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var chipGroup: ChipGroup
    private lateinit var searchInput: TextInputEditText
    private lateinit var filterButton: MaterialButton

    private val allDoctors = mutableListOf<Doctor>()
    private var currentDoctors = listOf<Doctor>()
    private lateinit var adapter: DoctorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_discover_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.careGiversRecyclerView)
        loadingProgress = view.findViewById(R.id.loadingProgress)
        chipGroup = view.findViewById(R.id.filterChipGroup)
        searchInput = view.findViewById(R.id.searchInput)
        filterButton = view.findViewById(R.id.filterButton)

        // Setup adapter
        adapter = DoctorAdapter(
            onDoctorClick = { doctor ->
                showToast("Selected: ${doctor.name}")
            },
            onBookClick = { doctor ->
                val currentDate = java.util.Calendar.getInstance()

                val datePickerDialog = android.app.DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

                        val hour = currentDate.get(java.util.Calendar.HOUR_OF_DAY)
                        val minute = currentDate.get(java.util.Calendar.MINUTE)

                        val timePickerDialog = android.app.TimePickerDialog(
                            requireContext(),
                            { _, selectedHour, selectedMinute ->
                                val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)

                                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
                                val booking = Booking(
                                    userId = userId,
                                    doctorId = doctor.id ?: "",
                                    doctorName = doctor.name ?: "",
                                    date = selectedDate,
                                    time = timeString
                                )

                                val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
                                val bookingId = bookingRef.push().key ?: return@TimePickerDialog

                                bookingRef.child(bookingId).setValue(booking)
                                    .addOnSuccessListener {
                                        showToast("Booking saved for ${doctor.name} on $selectedDate at $timeString")
                                    }
                                    .addOnFailureListener {
                                        showToast("Failed to save booking: ${it.message}")
                                    }
                            },
                            hour,
                            minute,
                            false
                        )

                        timePickerDialog.setTitle("Select Booking Time")
                        timePickerDialog.show()
                    },
                    currentDate.get(java.util.Calendar.YEAR),
                    currentDate.get(java.util.Calendar.MONTH),
                    currentDate.get(java.util.Calendar.DAY_OF_MONTH)
                )

                datePickerDialog.setTitle("Select Booking Date")
                datePickerDialog.show()
            }
            ,
            onMessageClick = { doctor ->
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val doctorId = doctor.id
                val doctorUser = User(
                    name = doctor.name ?: "",
                    id = doctorId?:"",
                    lastmsg = ""
                )

                db.collection("users").document(doctorId?:"").get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            db.collection("users").document(doctorId?:"")
                                .set(doctorUser)
                                .addOnSuccessListener {
                                    showToast("Doctor added to users")
                                }
                                .addOnFailureListener {
                                    showToast("Failed to add doctor")
                                }
                        } else {
                            showToast("Doctor already exists")
                        }

                        // Always open chat after checking
                        val intent = Intent(requireContext(), PrivateChat::class.java)
                        intent.putExtra("receiverId", doctorId)
                        intent.putExtra("Name",doctor.name)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        showToast("Error checking user: ${it.message}")
                    }
            }

        )



        recyclerView.adapter = adapter

        setupSearch()
        setupFilters()

        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        loadDoctorsFromFirebase()
    }

    private fun loadDoctorsFromFirebase() {
        loadingProgress.visibility = View.VISIBLE
        val databaseRef = FirebaseDatabase.getInstance().getReference("Doctor")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allDoctors.clear()
                for (doctorSnapshot in snapshot.children) {
                    val doctor = doctorSnapshot.getValue(Doctor::class.java)
                    if (doctor != null) {
                        allDoctors.add(doctor)
                    }
                }

                currentDoctors = allDoctors
                adapter.submitList(currentDoctors)
                loadingProgress.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                loadingProgress.visibility = View.GONE
                showToast("Failed to load data: ${error.message}")
            }
        })
    }

    private fun setupSearch() {
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterDoctors()
                true
            } else false
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterDoctors()
            }
        })
    }

    private fun setupFilters() {
        filterButton.setOnClickListener {
            chipGroup.visibility =
                if (chipGroup.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        chipGroup.setOnCheckedStateChangeListener { _, _ ->
            filterDoctors()
        }
    }

    private fun filterDoctors() {
        val searchQuery = searchInput.text.toString().lowercase()

        val elderCareSelected = chipGroup.findViewById<Chip>(R.id.chipElderCare)?.isChecked ?: false
        val medicalVisitSelected = chipGroup.findViewById<Chip>(R.id.chipMedicalVisit)?.isChecked ?: false
        val physicalTherapySelected = chipGroup.findViewById<Chip>(R.id.chipPhysicalTherapy)?.isChecked ?: false
        val postSurgerySelected = chipGroup.findViewById<Chip>(R.id.chipPostSurgeryCare)?.isChecked ?: false
        val pediatricSelected = chipGroup.findViewById<Chip>(R.id.chipPediatricCare)?.isChecked ?: false
        val medicationManagementSelected = chipGroup.findViewById<Chip>(R.id.chipMedicationManagement)?.isChecked ?: false

        val anyFilterSelected = elderCareSelected || medicalVisitSelected || physicalTherapySelected ||
                postSurgerySelected || pediatricSelected || medicationManagementSelected

        currentDoctors = allDoctors.filter { doctor ->
            val matchesSearch = doctor.name.lowercase().contains(searchQuery) ||
                    doctor.specialty.lowercase().contains(searchQuery) ||
                    doctor.location.lowercase().contains(searchQuery)

            val specialty = doctor.specialty.lowercase()

            val matchesCategory = if (!anyFilterSelected) {
                true
            } else {
                (elderCareSelected && specialty.contains("elderly")) ||
                        (medicalVisitSelected && (specialty.contains("medical") || specialty.contains("physician"))) ||
                        (physicalTherapySelected && specialty.contains("physical therapy")) ||
                        (postSurgerySelected && specialty.contains("post surgery")) ||
                        (pediatricSelected && specialty.contains("pediatric")) ||
                        (medicationManagementSelected && specialty.contains("medication"))
            }

            matchesSearch && matchesCategory
        }

        adapter.submitList(currentDoctors)
    }
    private fun refreshData() {
        Handler(Looper.getMainLooper()).postDelayed({
            swipeRefreshLayout.isRefreshing = false
            loadDoctorsFromFirebase()
            showToast("Data refreshed")
        }, 1500)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

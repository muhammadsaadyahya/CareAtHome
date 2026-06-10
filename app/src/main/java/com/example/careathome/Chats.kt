package com.example.careathome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Chats : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsersAdapter
    private val userList = mutableListOf<User>()

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("ChatsFragment", "onCreateView: Inflating fragment layout")
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewUsers)
        Log.d("ChatsFragment", "onCreateView: RecyclerView initialized")

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        Log.d("ChatsFragment", "onCreateView: RecyclerView LayoutManager set")

        adapter = UsersAdapter(userList) { selectedUser ->
            Log.d("ChatsFragment", "onCreateView: User clicked with ID = ${selectedUser.id}")
            val intent = Intent(requireContext(), PrivateChat::class.java)
            intent.putExtra("receiverId", selectedUser.id)
            intent.putExtra("Name",selectedUser.name)
            startActivity(intent)
            Log.d("ChatsFragment", "onCreateView: Started PrivateChat Activity with receiverId = ${selectedUser.id}")
        }
        recyclerView.adapter = adapter
        Log.d("ChatsFragment", "onCreateView: Adapter set to RecyclerView")

        fetchChatUsers()

        return view
    }

    private fun fetchChatUsers() {
        Log.d("ChatsFragment", "fetchChatUsers: Called")
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            Log.d("ChatsFragment", "fetchChatUsers: Fetching chats where participants include $currentUserUid")

            db.collection("chats")
                .whereArrayContains("participants", currentUserUid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    Log.d("ChatsFragment", "fetchChatUsers: Successfully fetched chats snapshot with ${querySnapshot.size()} documents")

                    val otherParticipants = mutableSetOf<String>() // Use a set to avoid duplicates
                    for (document in querySnapshot.documents) {
                        val participants = document.get("participants") as? List<String> ?: emptyList()
                        for (participant in participants) {
                            if (participant != currentUserUid) {
                                otherParticipants.add(participant)
                            }
                        }
                    }

                    if (otherParticipants.isNotEmpty()) {
                        Log.d("ChatsFragment", "fetchChatUsers: Found other participants: $otherParticipants")
                        fetchUserDetails(otherParticipants.toList()) // ✅ You were missing this
                    } else {
                        Log.d("ChatsFragment", "fetchChatUsers: No other participants found.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ChatsFragment", "fetchChatUsers: Error fetching chats: ${e.message}")
                }
        } else {
            Log.e("ChatsFragment", "fetchChatUsers: Current user UID is null")
        }
    }


    private fun fetchUserDetails(ids: List<String>) {
        Log.d("ChatsFragment", "fetchUserDetails: Called with ids = $ids")

        if (ids.isEmpty()) {
            Log.d("ChatsFragment", "fetchUserDetails: No user IDs passed, returning early")
            return
        }




        userList.clear()

        Log.d("ChatsFragment", "fetchUserDetails: Cleared existing user list")
        val aiUser = User(
            id = "AI_BOT_001",
            name = "CareAtHome AI",
            lastmsg = "Hi! I'm your care assistant")
        userList.add(aiUser)
        for (id in ids) {
            db.collection("users").document(id).get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject(User::class.java)
                    if (user != null) {
                        // Fetch last message from Firestore
                        db.collection("chats")
                            .whereEqualTo("participants", listOf(currentUserId!!, id).sorted())
                            .limit(1)
                            .get()
                            .addOnSuccessListener { chatDocs ->
                                if (!chatDocs.isEmpty) {
                                    val chatId = chatDocs.documents[0].id
                                    db.collection("chats").document(chatId)
                                        .collection("messages")
                                        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                                        .limit(1)
                                        .get()
                                        .addOnSuccessListener { messageDocs ->
                                            if (!messageDocs.isEmpty) {
                                                val lastMsg = messageDocs.documents[0].getString("text") ?: ""
                                                userList.add(user.copy(lastmsg = lastMsg))
                                            } else {
                                                userList.add(user.copy(lastmsg = ""))
                                            }
                                            adapter.notifyDataSetChanged()
                                        }
                                } else {
                                    userList.add(user.copy(lastmsg = "No messages in this chat"))
                                    adapter.notifyDataSetChanged()
                                }
                            }
                    }
                }
        }

    }
}

package com.example.careathome

import android.util.Log
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PrivateChat : AppCompatActivity() {
    private var generativeModel: GenerativeModel? = null

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageView
    private lateinit var ContactName: TextView
    private lateinit var Backbtn: ImageView

    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private lateinit var receiverId: String
    private lateinit var ChatUser: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d("PrivateChat", "onCreate: Activity started")
        setContentView(R.layout.activity_private_chat)

        receiverId = intent.getStringExtra("receiverId") ?: ""
        ChatUser = intent.getStringExtra("Name") ?: ""
        ContactName=findViewById(R.id.contactName)
        ContactName.text=ChatUser
        Backbtn=findViewById(R.id.backIcon)

        Backbtn.setOnClickListener{
            finish()
        }



        Log.d("PrivateChat", "onCreate: receiverId = $receiverId")

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        adapter = ChatAdapter(messages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = adapter




        listenForMessages()



        Log.d("PrivateChat", "onCreate: RecyclerView and Adapter set up")
        if(receiverId!="AI_BOT_001") {

            sendButton.setOnClickListener {
                Log.d("PrivateChat", "sendButton clicked")
                val text = messageInput.text.toString()
                if (text.isNotEmpty()) {
                    Log.d("PrivateChat", "Sending message: $text")
                    sendMessage(text)
                } else {
                    Log.d("PrivateChat", "Empty message input, ignoring send")
                }

            }

        }
        else{
            val apiKey = "AIzaSyCqf9kx6fQbtU8Y7dh61VEzbhFumdYhJPU"


            generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )
            sendButton.setOnClickListener {
                Log.d("PrivateChat", "sendButton clicked")
                val text = messageInput.text.toString()
                if (text.isNotEmpty()) {
                    Log.d("PrivateChat", "Sending message: $text")
                    sendMessageAi(text)
                } else {
                    Log.d("PrivateChat", "Empty message input, ignoring send")
                }

            }

        }


    }
    
    private fun sendMessageAi(userInput: String) {
        val chatId = getChatId(userId, receiverId)
        val chatDocRef = db.collection("chats").document(chatId)

        val userMessage = Message(
            text = userInput,
            senderId = userId,
            receiverId = receiverId,
            timestamp = System.currentTimeMillis()
        )

        sendMessageToChat(chatDocRef, userMessage)

        val systemPrompt = """
    You are an expert AI caregiver assistant. Only answer questions related to home care, health, or app support. Politely decline unrelated questions.
""".trimIndent()

        val prompt = content {
            text("$systemPrompt\nUser: $userInput")
        }


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = generativeModel?.generateContent(prompt)
                val aiText = response?.text ?: "I'm not sure how to respond to that."

                val aiMessage = Message(
                    text = aiText,
                    senderId = receiverId,
                    receiverId = userId,
                    timestamp = System.currentTimeMillis()
                )

                withContext(Dispatchers.Main) {
                    sendMessageToChat(chatDocRef, aiMessage)
                }
            } catch (e: Exception) {
                Log.e("PrivateChat", "sendMessageAi: Error generating content: ${e.message}")
            }
        }
    }


    private fun sendMessage(text: String) {
        val chatId = getChatId(userId, receiverId)
        val chatDocRef = db.collection("chats").document(chatId)
        Log.d("PrivateChat", "sendMessage: Generated ChatID = $chatId")

        val message = Message(
            text = text,
            senderId = userId,
            receiverId = receiverId,
            timestamp = System.currentTimeMillis()
        )

        //check for the existence of the document
        chatDocRef.get().addOnSuccessListener { docSnapshot ->
            if (docSnapshot.exists()) {
                // Document exists, send the message
                Log.d("PrivateChat", "sendMessage: Chat document exists. Sending message.");
                sendMessageToChat(chatDocRef, message);
            } else {
                //Document does not exist, create it and send the message
                Log.d("PrivateChat", "sendMessage: Chat document does not exist. Creating and sending message.");
                val sortedParticipants = listOf(userId, receiverId).sorted()
                val chatData = hashMapOf(
                    "participants" to sortedParticipants
                )

                chatDocRef.set(chatData).addOnSuccessListener {
                    Log.d("PrivateChat", "sendMessage: Chat document created.");
                    sendMessageToChat(chatDocRef, message);
                }.addOnFailureListener { e ->
                    Log.e("PrivateChat", "sendMessage: Failed to create chat document: ${e}");
                }
            }

        }.addOnFailureListener { e ->
            Log.e("PrivateChat", "sendMessage: Error getting chat document: ${e.message}")
        }
    }


    private fun sendMessageToChat(chatDocRef: DocumentReference, message: Message) {
        Log.d("PrivateChat", "sendMessageToChat: Attempting to send message: ${message.text}")

        chatDocRef.collection("messages")
            .add(message)
            .addOnSuccessListener {
                Log.d("PrivateChat", "sendMessageToChat: Message sent successfully")
                messageInput.text.clear()
            }
            .addOnFailureListener { e ->
                Log.e("PrivateChat", "sendMessageToChat: Failed to send message: ${e.message}")
            }
    }

    private fun listenForMessages() {
        val chatId = getChatId(userId, receiverId)
        Log.d("PrivateChat", "listenForMessages: Setting up listener for ChatID = $chatId")

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PrivateChat", "listenForMessages: Error listening to messages: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d("PrivateChat", "listenForMessages: Received ${snapshot.size()} messages")
                    messages.clear()
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(Message::class.java)
                        if (message != null) {
                            Log.d("PrivateChat", "listenForMessages: Adding message: ${message.text}")
                            messages.add(message)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messages.size - 1)
                } else {
                    Log.e("PrivateChat", "listenForMessages: Snapshot is null")
                }
            }
    }

    private fun getChatId(user1: String, user2: String): String {
        val chatId = if (user1 < user2) {
            "$user1-$user2"
        } else {
            "$user2-$user1"
        }
        Log.d("PrivateChat", "getChatId: Generated ChatID = $chatId")
        return chatId
    }
}

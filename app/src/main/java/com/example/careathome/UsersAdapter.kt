package com.example.careathome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter(private val users: List<User>, private val onUserClicked: (User) -> Unit) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.userName)
        val lastText: TextView = view.findViewById(R.id.userMessage)
        val profileImage: ImageView = view.findViewById(R.id.profileImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        holder.nameText.text = user.name
        holder.lastText.text = user.lastmsg
        holder.profileImage.setImageResource(R.drawable.ic_profile)

        holder.itemView.setOnClickListener {
            onUserClicked(user)
        }
    }

    override fun getItemCount() = users.size
}

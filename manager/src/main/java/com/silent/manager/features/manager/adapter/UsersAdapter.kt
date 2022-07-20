package com.silent.manager.features.manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.slideInBottom
import com.silent.core.databinding.PodcastIconLayoutBinding
import com.silent.core.users.User
import com.silent.manager.R
import com.silent.manager.databinding.UsersIconLayoutBinding

class UsersAdapter(val users: ArrayList<User>, val onClickUser: (User) -> Unit): RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val user = users[bindingAdapterPosition]
            UsersIconLayoutBinding.bind(itemView).run {
                Glide.with(root.context).load(user.profilePic).into(userPic)
                userName.text = user.name
                root.slideInBottom()
                userPic.setOnClickListener {
                    onClickUser(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.users_icon_layout, parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = users.size

}
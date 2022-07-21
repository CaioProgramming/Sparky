package com.silent.manager.features.manager.users.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.ilustris.ui.extensions.gone
import com.ilustris.ui.extensions.visible
import com.silent.core.users.NEW_USER
import com.silent.core.users.User
import com.silent.manager.R
import com.silent.manager.databinding.PodcastHorizontalLayoutBinding

class UserListAdapter(val users: List<User>, val selectUser: (User) -> Unit): RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {


    inner class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind() {
            val user = users[bindingAdapterPosition]
            PodcastHorizontalLayoutBinding.bind(itemView).run {
                root.setOnClickListener {
                    selectUser(user)
                }
                programIcon.borderWidth = 0
                if (user.id != NEW_USER) {
                    updateProgress.visible()
                    if (user.admin) updateProgress.rimColor = ContextCompat.getColor(itemView.context, R.color.material_yellow700)  else updateProgress.gone()
                    updateCheck.gone()
                    Glide.with(itemView).load(user.profilePic).into(programIcon)
                    podcastTitle.text = user.name
                    updateProgress.progress = 100
                } else {
                    updateProgress.gone()
                    podcastTitle.gone()
                    updateCheck.gone()
                    Glide.with(itemView).load(R.drawable.ic_iconmonstr_plus).fitCenter().into(programIcon)
                }
                root.fadeIn()
            }
        }
    }



    override fun getItemCount(): Int = users.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.podcast_horizontal_layout, parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
       holder.bind()
    }

}
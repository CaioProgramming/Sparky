package com.silent.sparky.features.podcast.schedule

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ilustris.animations.fadeIn
import com.ilustris.animations.popIn
import com.silent.core.podcast.Host
import com.silent.ilustriscore.core.utilities.*
import com.silent.sparky.R
import com.silent.sparky.databinding.TodayGuestLayoutBinding

class SchedulePagerAdapter(val guestList: List<Host>, val highlightColor: String) :RecyclerView.Adapter<SchedulePagerAdapter.SchedulePageHolder>(){


    inner class SchedulePageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            TodayGuestLayoutBinding.bind(itemView).run {
                val guest = guestList[bindingAdapterPosition]
                iconPlaceHolder.imageTintList = ColorStateList.valueOf(Color.parseColor(highlightColor))
                hostNameCard.setStrokeColor(ColorStateList.valueOf(Color.parseColor(highlightColor)))
                hostCard.setStrokeColor(ColorStateList.valueOf(Color.parseColor(highlightColor)))
                Glide.with(itemView.context).load(guest.profilePic).error(R.drawable.ic_iconmonstr_connection_1).into(hostPhoto)
                hostName.text = guest.name
                hostName.setTextColor(Color.parseColor(highlightColor))
                hostDescription.text = guest.description
                if (guest.isComingToday()) {
                    eventDate.text = "Hoje!"
                } else {
                    eventDate.text = guest.comingDate?.formatDate("dd.MM - HH") + "H"
                }
                root.popIn()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchedulePageHolder {
      return  SchedulePageHolder(LayoutInflater.from(parent.context).inflate(R.layout.today_guest_layout, parent, false))
    }

    override fun onBindViewHolder(holder: SchedulePageHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = guestList.size


}
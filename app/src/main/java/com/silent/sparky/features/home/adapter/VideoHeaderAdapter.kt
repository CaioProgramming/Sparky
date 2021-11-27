package com.silent.sparky.features.home.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ilustris.animations.fadeIn
import com.silent.ilustriscore.core.utilities.gone
import com.silent.sparky.R
import com.silent.sparky.data.PodcastHeader
import com.silent.sparky.data.programSections
import com.silent.sparky.features.podcast.adapter.VideosAdapter
import kotlinx.android.synthetic.main.video_group_layout.view.*

class VideoHeaderAdapter(
    val programSections: programSections,
    val headerSelected: (PodcastHeader) -> Unit,
    val iconClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<VideoHeaderAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            programSections[adapterPosition].run {
                itemView.group_title.text = title
                itemView.group_title.setOnClickListener {
                    headerSelected(this)
                }
                itemView.see_more_button.setOnClickListener {
                    headerSelected(this)
                }
                iconClick?.let {
                    itemView.program_icon.setOnClickListener { _ ->
                        it.invoke(channelURL!!)
                    }
                }
                itemView.videos_recycler.adapter = VideosAdapter(videos)
                Glide.with(itemView.context)
                    .load(icon)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            itemView.program_icon.gone()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            itemView.program_icon.setImageDrawable(resource)
                            return false
                        }
                    })
                    .into(itemView.program_icon)
                icon?.let {
                    itemView.program_icon.fadeIn()
                } ?: run {
                    itemView.program_icon.gone()
                }
                itemView.videos_recycler.layoutManager =
                    LinearLayoutManager(itemView.context, orientation, false)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.video_group_layout, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = programSections.count()

    fun updateSection(podcastHeader: PodcastHeader) {
        programSections.add(podcastHeader)
        notifyItemInserted(itemCount)
    }

    fun clearAdapter() {
        programSections.clear()
        notifyDataSetChanged()
    }

}
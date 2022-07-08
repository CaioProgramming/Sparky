package com.silent.sparky.features.home.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ilustris.animations.fadeIn
import com.ilustris.ui.extensions.gone
import com.silent.core.videos.Video
import com.silent.sparky.R
import com.silent.sparky.databinding.VideoGroupLayoutBinding
import com.silent.sparky.features.home.data.PodcastHeader
import com.silent.sparky.features.home.data.programSections
import com.silent.sparky.features.podcast.adapter.VideosAdapter

class VideoHeaderAdapter(
    val programSections: programSections,
    val headerSelected: (PodcastHeader) -> Unit,
    val iconClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<VideoHeaderAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            VideoGroupLayoutBinding.bind(itemView).run {
                val section = programSections[bindingAdapterPosition]
                setupHeader(section)
                val layoutManager = LinearLayoutManager(itemView.context, section.orientation, false)
                val maxLimit = if (section.videos.size > 20) 20 else section.videos.size
                videosRecycler.adapter = VideosAdapter(section.videos.subList(0, maxLimit), section.highLightColor)
                videosRecycler.layoutManager = layoutManager
                if (section.scrollAnimation) {
                    videosRecycler.smoothScrollBy(100, 100, {
                        50f
                    }, 1000)
                }
                section.referenceIndex?.let {
                    videosRecycler.scrollToPosition(it)
                    section.referenceIndex = null
                }
            }
        }

        private fun VideoGroupLayoutBinding.setupHeader(section: PodcastHeader) {
            section.highLightColor?.let {
                seeMoreButton.imageTintList =
                    ColorStateList.valueOf(Color.parseColor(it))
                programIcon.borderColor = Color.parseColor(it)
            }
            groupTitle.text = section.title
            groupTitle.setOnClickListener {
                headerSelected(section)
            }
            seeMoreButton.setOnClickListener {
                headerSelected(section)
            }
            iconClick?.let {
                programIcon.setOnClickListener { _ ->
                    headerSelected(section)
                }
            }
            Glide.with(itemView.context)
                .load(section.icon)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        programIcon.gone()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        programIcon.setImageDrawable(resource)
                        return false
                    }
                })
                .into(programIcon)
            section.icon?.let {
                if (!programIcon.isVisible) {
                    programIcon.fadeIn()
                }
            } ?: run {
                programIcon.gone()
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

    override fun getItemCount() = programSections.size

    fun updateSection(position: Int, videos: ArrayList<Video>, index: Int) {
        val header = programSections[position]
        header.videos.addAll(videos)
        header.referenceIndex = index
        notifyItemChanged(position, header)
    }

    fun addSections(headers: ArrayList<PodcastHeader>) {
        programSections.addAll(headers)
        notifyItemRangeChanged(0, programSections.size)
    }

    fun clearAdapter() {
        programSections.clear()
        notifyDataSetChanged()
    }

}
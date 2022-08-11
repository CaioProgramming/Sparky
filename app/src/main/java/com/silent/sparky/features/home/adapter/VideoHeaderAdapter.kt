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
import com.ilustris.ui.extensions.visible
import com.silent.core.component.PodcastAdapter
import com.silent.core.databinding.VideoGroupLayoutBinding
import com.silent.core.podcast.HeaderType
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastHeader
import com.silent.core.podcast.programSections
import com.silent.core.videos.Video
import com.silent.sparky.R
import com.silent.sparky.features.podcast.adapter.VideosAdapter

class VideoHeaderAdapter(
    val programSections: programSections,
    val headerSelected: (PodcastHeader) -> Unit,
    val iconClick: ((String) -> Unit)? = null,
    val selectPodcast: ((Podcast) -> Unit)? = null,
    val onVideoClick: (Video, Podcast) -> Unit
) : RecyclerView.Adapter<VideoHeaderAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            VideoGroupLayoutBinding.bind(itemView).run {
                val section = programSections[bindingAdapterPosition]
                setupHeader(section)
                if (section.type == HeaderType.VIDEOS) {
                    val layoutManager =
                        LinearLayoutManager(itemView.context, section.orientation, false)
                    val maxLimit = if (section.videos!!.size > 20) 20 else section.videos!!.size
                    videosRecycler.adapter = VideosAdapter(
                        section.videos!!.subList(0, maxLimit),
                        section.highLightColor
                    ) { video ->
                        section.podcast?.let {
                            onVideoClick(video, it)
                        }
                    }
                    videosRecycler.layoutManager = layoutManager
                } else {
                    videosRecycler.layoutManager =
                        LinearLayoutManager(itemView.context, section.orientation, false)
                    videosRecycler.adapter =
                        PodcastAdapter(ArrayList(section.podcasts!!), section.showTitle) {
                            selectPodcast?.invoke(it)
                        }
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
            groupSubtitle.text = section.subTitle
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
            section.icon?.let {
                if (!programIcon.isVisible) {
                    programIcon.fadeIn()
                }
            } ?: run {
                programIcon.gone()
            }
            if (section.seeMore) seeMoreButton.visible() else seeMoreButton.gone()
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
            if (section.showDivider) root.setBackgroundResource(R.drawable.bottom_line)
            root.backgroundTintList =
                ColorStateList.valueOf(root.context.getColor(R.color.material_grey900))
            if (section.showTitle) headerInfo.visible() else headerInfo.gone()
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

}
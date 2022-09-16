package com.silent.manager.features.podcast.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.ilustris.ui.extensions.gone
import com.ilustris.ui.extensions.visible
import com.silent.core.utils.ImageUtils
import com.silent.core.videos.Video
import com.silent.ilustriscore.core.utilities.DateFormats
import com.silent.ilustriscore.core.utilities.format
import com.silent.manager.R
import com.silent.manager.databinding.VideoPreviewStackedBinding

class VideosAdapter(
    val playlistVideos: List<Video>,
    private val highlightColor: String? = null,
    val onSelectVideo: ((Video) -> Unit)? = null
) : RecyclerView.Adapter<VideosAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val video = playlistVideos[bindingAdapterPosition]
            VideoPreviewStackedBinding.bind(itemView).run {
                highlightColor?.let {
                    videoCard.setStrokeColor(ColorStateList.valueOf(Color.parseColor(it)))
                }
                Glide.with(itemView.context).load(ImageUtils.getYoutubeThumb(video.youtubeID))
                    .error(video.podcast?.iconURL).into(videoThumb)
                title.text = video.title
                try {
                    publishDate.text =
                        video.publishedAt.format(DateFormats.DD_OF_MM_FROM_YYYY)
                    publishDate.visible()
                } catch (e: Exception) {
                    publishDate.gone()
                }
                if (!root.isVisible) {
                    root.fadeIn()
                }
                root.setOnClickListener {
                    onSelectVideo?.invoke(video)
                }
                root.contentDescription = video.title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.video_preview_stacked, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = playlistVideos.count()


}
package com.silent.sparky.features.podcast.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.utils.WebUtils
import com.silent.core.youtube.PlaylistResource
import com.silent.ilustriscore.core.utilities.DateFormats
import com.silent.ilustriscore.core.utilities.formatDate
import com.silent.sparky.R
import com.silent.sparky.databinding.VideoPreviewBinding

class VideosAdapter(
    val playlistVideos: List<PlaylistResource>,
    private val highlightColor: String? = null
) : RecyclerView.Adapter<VideosAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val video = playlistVideos[adapterPosition].snippet
            VideoPreviewBinding.bind(itemView).run {
                if (highlightColor != null) {
                    videoCard.setStrokeColor(
                        ColorStateList.valueOf(
                            Color.parseColor(
                                highlightColor
                            )
                        )
                    )
                }
                videoCard.setOnClickListener {
                    WebUtils(itemView.context).openYoutubeVideo(video.resourceId.videoId)
                }
                try {
                    Glide.with(itemView.context).load(video.thumbnails.standard.url)
                        .into(videoThumb)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(javaClass.simpleName, "bind: erro ao carregar thumbnail de vídeo $video")
                }
                title.text = video.title
                publishDate.text = video.publishedAt.formatDate(
                    video.publishedAt,
                    DateFormats.DD_OF_MM_FROM_YYYY.format
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val bind =
            LayoutInflater.from(parent.context).inflate(R.layout.video_preview, parent, false)
        return VideoViewHolder(bind)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = playlistVideos.count()


}
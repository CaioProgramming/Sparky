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
import kotlinx.android.synthetic.main.cut_player_layout.view.video_card
import kotlinx.android.synthetic.main.video_preview.view.*

class VideosAdapter(
    val playlistVideos: List<PlaylistResource>,
    private val highlightColor: String? = null
) : RecyclerView.Adapter<VideosAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val video = playlistVideos[adapterPosition].snippet
            if (highlightColor != null) {
                itemView.video_card.setStrokeColor(
                    ColorStateList.valueOf(
                        Color.parseColor(
                            highlightColor
                        )
                    )
                )
            }
            itemView.video_card.setOnClickListener {
                WebUtils(itemView.context).openYoutubeVideo(video.resourceId.videoId)
            }
            try {
                Glide.with(itemView.context).load(video.thumbnails.standard.url)
                    .into(itemView.video_thumb)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(javaClass.simpleName, "bind: erro ao carregar thumbnail de vídeo $video")
            }
            itemView.title.text = video.title
            itemView.publish_date.text = video.publishedAt.formatDate(
                video.publishedAt,
                DateFormats.DD_OF_MM_FROM_YYYY.format
            )

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
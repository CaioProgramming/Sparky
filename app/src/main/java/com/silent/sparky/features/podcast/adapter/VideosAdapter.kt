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
import com.silent.core.videos.Video
import com.silent.ilustriscore.core.utilities.gone
import com.silent.sparky.R
import com.silent.sparky.databinding.VideoPreviewBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class VideosAdapter(
    val playlistVideos: List<Video>,
    private val highlightColor: String? = null
) : RecyclerView.Adapter<VideosAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val video = playlistVideos[adapterPosition]
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
                    WebUtils(itemView.context).openYoutubeVideo(video.youtubeID)
                }
                try {
                    Glide.with(itemView.context).load(video.thumbnail_url)
                        .into(videoThumb)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(javaClass.simpleName, "bind: erro ao carregar thumbnail de vídeo $video")
                }
                title.text = video.title
                try {
                    val date =
                        LocalDate.parse(video.publishedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            .format(DateTimeFormatter.ofPattern("YYYY-MM-DDThh:mm:ss.sZ"))
                    publishDate.text = date
                } catch (e: Exception) {
                    publishDate.gone()
                }
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
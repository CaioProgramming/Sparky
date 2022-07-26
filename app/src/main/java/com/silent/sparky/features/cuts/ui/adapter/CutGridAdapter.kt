package com.silent.sparky.features.cuts.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.silent.core.utils.ImageUtils
import com.silent.core.videos.Video
import com.silent.core.videos.VideoType
import com.silent.sparky.R
import com.silent.sparky.databinding.CutBigPreviewBinding
import com.silent.sparky.databinding.CutMediumPreviewBinding
import com.silent.sparky.databinding.CutPreviewBinding

class CutGridAdapter(val cuts: ArrayList<Video>, val onSelectVideo: (Video) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class CutBigViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val cut = cuts[bindingAdapterPosition]
            CutBigPreviewBinding.bind(itemView).run {
                Glide.with(itemView.context)
                    .load(ImageUtils.getYoutubeThumb(cut.youtubeID, ImageUtils.Quality.MAX))
                    .error(cut.podcast?.iconURL)
                    .into(videoThumb)
                cut.podcast?.let {
                    Glide.with(itemView.context).load(it.iconURL).into(podcastIcon)
                    val highlightColor = Color.parseColor(it.highLightColor)
                    videoCard.strokeColor = highlightColor
                    podcastIcon.borderColor = highlightColor
                }
                root.fadeIn()
            }
        }
    }

    inner class CutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            val cut = cuts[bindingAdapterPosition]
            CutPreviewBinding.bind(itemView).run {
                Glide.with(itemView.context)
                    .load(ImageUtils.getYoutubeThumb(cut.youtubeID, ImageUtils.Quality.HIGH))
                    .error(cut.podcast?.iconURL)
                    .into(videoThumb)
                cut.podcast?.let {
                    Glide.with(itemView.context).load(it.iconURL).into(podcastIcon)
                    val highlightColor = Color.parseColor(it.highLightColor)
                    videoCard.strokeColor = highlightColor
                    podcastIcon.borderColor = highlightColor
                }
                root.fadeIn()
            }
        }
    }

    inner class CutMediumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            val cut = cuts[bindingAdapterPosition]
            CutMediumPreviewBinding.bind(itemView).run {
                Glide.with(itemView.context)
                    .load(ImageUtils.getYoutubeThumb(cut.youtubeID, ImageUtils.Quality.HIGH))
                    .error(cut.podcast?.iconURL)
                    .into(videoThumb)
                cut.podcast?.let {
                    Glide.with(itemView.context).load(it.iconURL).into(podcastIcon)
                    val highlightColor = Color.parseColor(it.highLightColor)
                    videoCard.strokeColor = highlightColor
                    podcastIcon.borderColor = highlightColor
                }
                root.fadeIn()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return cuts[position].videoType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (VideoType.values()[viewType]) {
            VideoType.DEFAULT -> CutViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.cut_preview, parent, false)
            )
            VideoType.BIG -> CutBigViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.cut_big_preview, parent, false)
            )
            VideoType.MEDIUM -> CutMediumViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.cut_medium_preview, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onSelectVideo(cuts[position])
        }
        when (holder) {
            is CutViewHolder -> holder.bind()
            is CutMediumViewHolder -> holder.bind()
            else -> (holder as CutBigViewHolder).bind()
        }

    }

    override fun getItemCount(): Int = cuts.size

}
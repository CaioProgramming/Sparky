package com.silent.sparky.features.cuts.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.codeboy.pager2_transformers.Pager2_ZoomOutSlideTransformer
import com.ilustris.ui.extensions.setAlpha
import com.silent.core.podcast.Podcast
import com.silent.sparky.R
import com.silent.sparky.databinding.CutPageLayoutBinding
import com.silent.sparky.features.cuts.data.PodcastCutHeader

class PodcastCutPageAdapter(
    val cutHeaders: ArrayList<PodcastCutHeader>, private val onSelectPodcast: (Podcast) -> Unit
) : RecyclerView.Adapter<PodcastCutPageAdapter.CutHeaderHolder>() {

    var enabledPlayer = 0

    fun updateEnabledPlayer(position: Int) {
        enabledPlayer = position
        notifyItemChanged(position)
    }

    inner class CutHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            val cutHeader = cutHeaders[bindingAdapterPosition]
            CutPageLayoutBinding.bind(itemView).run {
                val highlightColor = Color.parseColor(cutHeader.podcast.highLightColor)
                podcastName.text = cutHeader.podcast.name
                Glide.with(itemView).load(cutHeader.podcast.iconURL)
                    .error(R.drawable.ic_iconmonstr_connection_1).into(podcastIcon)
                podcastIcon.borderColor = highlightColor
                root.setStrokeColor(ColorStateList.valueOf(highlightColor))
                podcastIcon.setOnClickListener {
                    onSelectPodcast(cutHeader.podcast)
                }
                val cutsAdapter = CutsAdapter(cutHeader.videos)
                podcastCuts.adapter = cutsAdapter
                cutsProgress.max = cutHeader.videos.lastIndex
                cutsProgress.setIndicatorColor(highlightColor)
                cutsProgress.trackColor = highlightColor.setAlpha(0.5f)
                podcastCuts.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        cutsProgress.setProgress(position, true)
                        cutsAdapter.enabled = enabledPlayer == bindingAdapterPosition
                        cutsAdapter.initializeCut(position)
                    }
                })

                podcastCuts.setPageTransformer(Pager2_ZoomOutSlideTransformer())
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CutHeaderHolder {
        return CutHeaderHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cut_page_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CutHeaderHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return cutHeaders.size
    }

}
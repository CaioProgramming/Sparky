package com.silent.manager.features.newpodcast.fragments.youtube

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.component.PodcastAdapter
import com.silent.core.podcast.Podcast
import com.silent.manager.R
import com.silent.manager.databinding.PodcastsHeaderBinding

class PodcastHeaderAdapter(
    val podcastHeaders: ArrayList<PodcastsHeader>,
    val titleColor: Int = Color.WHITE,
    val onSelectPodcast: (Podcast) -> Unit
) : RecyclerView.Adapter<PodcastHeaderAdapter.PodcastHeaderHolder>() {

    fun updateHeaders(headers: ArrayList<PodcastsHeader>) {
        podcastHeaders.addAll(headers)
        notifyItemRangeInserted(0, headers.size)
    }

    inner class PodcastHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            PodcastsHeaderBinding.bind(itemView).run {
                val header = podcastHeaders[bindingAdapterPosition]
                groupTitle.text = header.title
                groupTitle.setTextColor(titleColor)
                channelsRecycler.adapter = PodcastAdapter(header.podcasts, true, onSelectPodcast)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastHeaderHolder {
        return PodcastHeaderHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.podcasts_header, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PodcastHeaderHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return podcastHeaders.size
    }

    fun clearAdapter() {
        podcastHeaders.clear()
        notifyDataSetChanged()
    }

}
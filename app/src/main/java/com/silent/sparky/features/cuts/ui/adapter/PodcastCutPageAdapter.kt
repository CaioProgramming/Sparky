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
    val cutHeaders: ArrayList<PodcastCutHeader>, private val onSelectPodcast: (Podcast) -> Unit,
    private val callNextPodcast: () -> Unit,
    private val callPreviousPodcast: () -> Unit
) : RecyclerView.Adapter<PodcastCutPageAdapter.CutHeaderHolder>() {


    inner class CutHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            val cutHeader = cutHeaders[bindingAdapterPosition]
            CutPageLayoutBinding.bind(itemView).run {
                val highlightColor = Color.parseColor(cutHeader.podcast.highLightColor)
                val gestureDetector =
                    pageGestureDetector(itemView.context, podcastCuts, cutHeader.videos.lastIndex)
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
                podcastCuts.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {

                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        cutsProgress.setProgress(position, true)
                        cutsAdapter.initializeCut(position)
                    }
                })

                podcastCuts.setPageTransformer(Pager2_ZoomOutSlideTransformer())
            }

        }

        fun pageGestureDetector(context: Context, pager: ViewPager2, count: Int): GestureDetector {
            return GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    val width = pager.width
                    val x = e?.x
                    val currentPage = pager.currentItem
                    x?.let {
                        if (x < (width * 0.5)) {
                            if (currentPage > 0) {
                                pager.setCurrentItem(currentPage - 1)
                            } else {
                                callPreviousPodcast()
                            }
                        } else {
                            if (currentPage < count) {
                                pager.setCurrentItem(currentPage + 1, true)
                            }
                        }
                    }
                    return true
                }
            })
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
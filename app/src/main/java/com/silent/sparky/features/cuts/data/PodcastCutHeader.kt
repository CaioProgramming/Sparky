package com.silent.sparky.features.cuts.data

import com.silent.core.podcast.Podcast
import com.silent.core.videos.Video

data class PodcastCutHeader(val podcast: Podcast, val videos: ArrayList<Video>)
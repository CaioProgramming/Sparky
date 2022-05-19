package com.silent.core.component

import com.silent.core.podcast.Host

data class HostGroup(
    val title: String,
    val hosts: List<Host>,
    val groupType: GroupType = GroupType.HOSTS
)

enum class GroupType {
    HOSTS, GUESTS
}

package com.silent.core

import com.silent.ilustriscore.core.bean.BaseBean

data class Program(override var id: String = "",
                   var name: String = "",
                   var iconURL: String = "",
                   var hosts: List<String> = emptyList(),
                   var twitter: String = "",
                   var youtube: String = "",
                   var instagram: String = "",
                   var twitch: String = ""): BaseBean(id)
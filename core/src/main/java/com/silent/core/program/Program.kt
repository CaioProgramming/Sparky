package com.silent.core.program

import com.silent.ilustriscore.core.bean.BaseBean

data class Program(override var id: String = "",
                   var name: String = "",
                   var iconURL: String = "",
                   var hosts: List<String> = emptyList(),
                   var youtubeID: String = "",
                   var cuts: String = ""): BaseBean(id)
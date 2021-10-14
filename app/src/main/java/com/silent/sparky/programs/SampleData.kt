package com.silent.sparky.programs

import com.silent.core.Program

object SampleData {

    fun programs() : List<Program> {
        return  listOf(
            Program(
                name = "Flow",
                iconURL = "https://yt3.ggpht.com/ytc/AKedOLSRQ_ImjA8y3k0NuQ4q9aOLd_lGKI17iq-3axURrw=s88-c-k-c0x00ffffff-no-rj"),
            Program(name = "Venus",
                iconURL = "https://yt3.ggpht.com/ytc/AKedOLRyY8OBDu_GMva3fG_t3EvuspaaJBOL2VvWwC_u=s88-c-k-c0x00ffffff-no-rj"),
            Program(name = "À Deriva",
                iconURL = "https://yt3.ggpht.com/ytc/AKedOLQVji9eZQSmMs2alFKbtCqyNbQoaoY1t9wNMlr1=s88-c-k-c0x00ffffff-no-rj")
        )

    }

}
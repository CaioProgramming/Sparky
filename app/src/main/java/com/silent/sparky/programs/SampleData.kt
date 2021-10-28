package com.silent.sparky.programs

import com.silent.core.program.Host
import com.silent.core.program.Program

object SampleData {

    fun programs() : List<Program> {
        return  listOf(
            Program(
                name = "Flow",
                youtubeID = "UC4ncvgh5hFr5O83MH7-jRJg",
                hosts = listOf(
                    Host(name = "Igor 3k", profilePic = "https://instagram.fcgh2-1.fna.fbcdn.net/v/t51.2885-19/s320x320/192332834_2496302097180258_3781854935235845887_n.jpg?_nc_ht=instagram.fcgh2-1.fna.fbcdn.net&_nc_ohc=TUlN2f9iaxIAX_lAYiI&edm=ABfd0MgBAAAA&ccb=7-4&oh=713be97df82802d40e53036c7ac6309e&oe=6179DA63&_nc_sid=7bff83"),
                    Host(name = "Monark", profilePic = "https://instagram.fcgh2-1.fna.fbcdn.net/v/t51.2885-19/s320x320/240662484_374011570975137_6489892659504550454_n.jpg?_nc_ht=instagram.fcgh2-1.fna.fbcdn.net&_nc_ohc=6l7k_DiaxtcAX9U1TMf&edm=ABfd0MgBAAAA&ccb=7-4&oh=67537333f4f0ad4e5fed0f62a7c14093&oe=6178E062&_nc_sid=7bff83")
                ),
                instagram = "flowpdc",
                twitch = "flowpodcast",
                cuts = "UU3uYvpJ3J6oNoNYRXfZXjEw",
                iconURL = "https://yt3.ggpht.com/ytc/AKedOLSRQ_ImjA8y3k0NuQ4q9aOLd_lGKI17iq-3axURrw=s88-c-k-c0x00ffffff-no-rj"),
            Program(name = "Venus",
                youtubeID = "UCTBhsXf_XRxk8w4rMj6WBOA",
                cuts = "UUOL_QmVnNQ5bKeS4zgqV81g",
                iconURL = "https://yt3.ggpht.com/ytc/AKedOLRyY8OBDu_GMva3fG_t3EvuspaaJBOL2VvWwC_u=s88-c-k-c0x00ffffff-no-rj"),
            Program(name = "À Deriva",
                youtubeID = "UCS8Mv2IWgrOIV5u52GzDuRQ",
                cuts = "UUeg3XXEiFL2Zr3HcfNPUVzg",
                iconURL = "https://yt3.ggpht.com/ytc/AKedOLQVji9eZQSmMs2alFKbtCqyNbQoaoY1t9wNMlr1=s88-c-k-c0x00ffffff-no-rj")
        )

    }

}
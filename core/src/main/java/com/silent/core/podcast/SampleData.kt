package com.silent.core.podcast

object SampleData {

    fun programs(): ArrayList<Podcast> {
        return ArrayList(
            listOf(
                Podcast(
                    id = "0",
                    name = "Flow",
                    youtubeID = "UC4ncvgh5hFr5O83MH7-jRJg",
                    hosts = listOf(
                        Host(
                            name = "Igor 3k",
                            profilePic = "https://instagram.fcgh2-1.fna.fbcdn.net/v/t51.2885-19/s320x320/192332834_2496302097180258_3781854935235845887_n.jpg?_nc_ht=instagram.fcgh2-1.fna.fbcdn.net&_nc_cat=106&_nc_ohc=cKXmn7x131oAX-g9O3q&edm=ABfd0MgBAAAA&ccb=7-4&oh=3a275f36a343629f8ea166a35f6f0098&oe=61A75623&_nc_sid=7bff83"
                        ),
                        Host(
                            name = "Monark",
                            profilePic = "https://instagram.fcgh2-1.fna.fbcdn.net/v/t51.2885-19/s320x320/240662484_374011570975137_6489892659504550454_n.jpg?_nc_ht=instagram.fcgh2-1.fna.fbcdn.net&_nc_cat=109&_nc_ohc=Tu05TCZfkXMAX_bDvZ4&edm=ABfd0MgBAAAA&ccb=7-4&oh=8d8a9505158d716208594bf7f93a16a6&oe=61A85662&_nc_sid=7bff83"
                        )
                    ),
                    instagram = "flowpdc",
                    twitch = "flowpodcast",
                    cuts = "UU3uYvpJ3J6oNoNYRXfZXjEw",
                    iconURL = "https://yt3.ggpht.com/ytc/AKedOLSRQ_ImjA8y3k0NuQ4q9aOLd_lGKI17iq-3axURrw=s88-c-k-c0x00ffffff-no-rj"
                ),
                Podcast(
                    id = "1",
                    name = "Venus",
                    youtubeID = "UCTBhsXf_XRxk8w4rMj6WBOA",
                    cuts = "UUOL_QmVnNQ5bKeS4zgqV81g",
                    iconURL = "https://yt3.ggpht.com/ytc/AKedOLRyY8OBDu_GMva3fG_t3EvuspaaJBOL2VvWwC_u=s88-c-k-c0x00ffffff-no-rj"
                ),
                Podcast(
                    id = "2",
                    name = "À Deriva",
                    youtubeID = "UCS8Mv2IWgrOIV5u52GzDuRQ",
                    cuts = "UUeg3XXEiFL2Zr3HcfNPUVzg",
                    iconURL = "https://yt3.ggpht.com/ytc/AKedOLQVji9eZQSmMs2alFKbtCqyNbQoaoY1t9wNMlr1=s88-c-k-c0x00ffffff-no-rj"
                )
            )
        )
    }

}
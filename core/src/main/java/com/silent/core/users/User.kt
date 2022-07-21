package com.silent.core.users

import com.silent.ilustriscore.core.bean.BaseBean

const val NEW_USER = "NEW_USER"
data class User(
    override var id: String = "",
    var uid: String = "",
    var flowUserName: String = "",
    var email: String = "",
    var name: String = "",
    var profilePic: String = "",
    var admin: Boolean = false
) : BaseBean(id) {
    companion object {
        fun newUser() = User(id = NEW_USER)
    }
}

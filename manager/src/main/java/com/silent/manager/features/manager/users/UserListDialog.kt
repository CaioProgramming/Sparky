package com.silent.manager.features.manager.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.silent.core.users.User
import com.silent.manager.databinding.UsersDialogBinding
import com.silent.manager.features.manager.users.adapter.UserListAdapter

const val USERS_TAG = "USERS_DIALOG"

class UserListDialog : BottomSheetDialogFragment() {

    var usersDialogBinding: UsersDialogBinding? = null
    lateinit var users: List<User>
    lateinit var onSelectUser: (User) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        usersDialogBinding = UsersDialogBinding.inflate(inflater)
        return usersDialogBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usersDialogBinding?.setupView()
    }

    private fun selectUser(user: User) {
        onSelectUser(user)
        dismiss()
    }

    private fun UsersDialogBinding.setupView() {
        message.text =  "${users.size}\nUsuários cadastrados"
        highlightColors.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        highlightColors.adapter = UserListAdapter(users, this@UserListDialog::selectUser)
        userSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    highlightColors.adapter = UserListAdapter(users.filter {
                        it.name.contains(query) || it.email.contains(query) || it.flowUserName.contains(
                            query
                        )
                    }, this@UserListDialog::selectUser)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    highlightColors.adapter = UserListAdapter(users.filter {
                        it.name.contains(newText) || it.email.contains(newText) || it.flowUserName.contains(
                            newText
                        )
                    }, this@UserListDialog::selectUser)
                }
                return false
            }

        })
        val closeButton: View? =
            userSearch.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeButton?.setOnClickListener {
            userSearch.setQuery("", false)
            highlightColors.adapter = UserListAdapter(users, this@UserListDialog::selectUser)
        }

    }

    companion object {
        fun getInstance(userList: List<User>, onUserClick: (User) -> Unit) =
            UserListDialog().apply {
                users = userList
                onSelectUser = onUserClick
            }
    }
}
package com.silent.manager.features.manager.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.silent.core.users.User
import com.silent.manager.databinding.HighlightColorDialogBinding
import com.silent.manager.features.manager.users.adapter.UserListAdapter

const val USERS_TAG = "USERS_DIALOG"
class UserListDialog : BottomSheetDialogFragment() {

    var highlightColorDialogBinding: HighlightColorDialogBinding? = null
    lateinit var users: List<User>
    lateinit var onSelectUser: (User) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        highlightColorDialogBinding = HighlightColorDialogBinding.inflate(inflater)
        return highlightColorDialogBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        highlightColorDialogBinding?.setupView()
    }

    private fun HighlightColorDialogBinding.setupView() {
        message.text = "Usuários cadastrados"
        highlightColors.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        highlightColors.adapter = UserListAdapter(users) {
            onSelectUser(it)
            dismiss()
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
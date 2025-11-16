package com.cs407.sharedspace.data

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserState(
    val id: Int = 0, val name: String = "", val uid: String = ""
)

class UserViewModel : ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    private val auth: FirebaseAuth = Firebase.auth
    val userState = _userState.asStateFlow()

    init {
        auth.addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                setUser(UserState())
            }
        }
    }

    fun setUser(state: UserState) {
        _userState.update {
            state
        }
    }
}
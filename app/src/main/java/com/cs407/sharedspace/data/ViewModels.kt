package com.cs407.sharedspace.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
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

    var userName by mutableStateOf("")
        private set

    fun loadUserName() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                userName = doc.getString("name") ?: ""
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to load user name", it)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to save user name", e)
            }
    }

    fun saveUserName(name: String, onComplete: () -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
            ?: return

        val uid = user.uid
        val db = FirebaseFirestore.getInstance()

        val data = hashMapOf(
            "name" to name,
            "email" to user.email
        )

        db.collection("users")
            .document(uid)
            .set(data)
            .addOnSuccessListener { onComplete() }
    }

    fun setUser(state: UserState) {
        _userState.update {
            state
        }
    }
}
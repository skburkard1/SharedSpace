package com.cs407.sharedspace.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserState(
    val id: Int = 0, val name: String = "", val uid: String = ""
)

//data class for group
data class Group(
    val groupId: String = "",
    val name: String = "",
    val members: List<String> = emptyList()
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

class GroupViewModel : ViewModel() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth


    fun createGroup(
        name: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ){

        val uid = auth.currentUser?.uid ?: return //uid or return if null
        val newDoc = db.collection("groups").document()
        val groupData = mapOf(
            "name" to name,
            "createdBy" to uid,
            "members" to listOf(uid)
        )

        newDoc.set(groupData)
            .addOnSuccessListener {
                db.collection("users")
                    .document(uid)
                    .update("groups", com.google.firebase.firestore.FieldValue.arrayUnion(newDoc.id))
                onSuccess(newDoc.id) //creation of new group
            }
            .addOnFailureListener {
                onFailure(it) //fails
            }
    }

    fun joinGroup( //just joining no creation
        groupId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return //uid or return if null
        val groupRef = db.collection("groups").document(groupId)

        viewModelScope.launch {
            try {
                val test = groupRef.get().await() //test null
                if (!test.exists()) {
                    onFailure(Exception("Does not exist.")) //doesnt exist
                    return@launch
                }
                groupRef.update(
                    "members", com.google.firebase.firestore.FieldValue.arrayUnion(uid)
                ).await()

                db.collection("users")
                    .document(uid)
                    .update("groups",  com.google.firebase.firestore.FieldValue.arrayUnion(groupId))
                        .await()

                onSuccess //successfully added
            } catch (e: Exception) {
                onFailure(e)
            }

        }

    }
    }

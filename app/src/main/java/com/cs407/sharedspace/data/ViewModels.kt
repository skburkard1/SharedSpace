package com.cs407.sharedspace.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
            } else {
                setUser(UserState(uid = user.uid))
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

/**
 * for creating/joining group
 */
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
                    .set(
                        mapOf("groups" to listOf(newDoc.id)),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
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
                    .set(
                        mapOf("groups" to listOf(groupId)),
                        com.google.firebase.firestore.SetOptions.merge()
                    )                        .await()
                onSuccess(groupId) //successfully added
            } catch (e: Exception) {
                onFailure(e)
            }

        }

    }
}
class GroupListViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups = _groups.asStateFlow()

    fun loadUserGroups() { //get all groupsuser is in
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(uid).get().await()
                val groupIds = userDoc.get("groups") as? List<String> ?: emptyList()
                val groupList = mutableListOf<Group>()

                for (id in groupIds) {
                    val groupDoc = db.collection("groups").document(id).get().await()
                    if (groupDoc.exists()) {
                        val name = groupDoc.getString("name") ?: "(Unnamed)"
                        groupList.add(Group(id, name))
                    }
                }
                _groups.value = groupList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}





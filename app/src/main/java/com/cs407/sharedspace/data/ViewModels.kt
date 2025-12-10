package com.cs407.sharedspace.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

    var currentGroupId: String? = null
        private set

    fun loadUserData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                userName = doc.getString("name") ?: ""

                val groups = doc.get("groups") as? List<String>
                currentGroupId = groups?.firstOrNull()

                Log.d("UserViewModel", "Loaded group ID: $currentGroupId")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to load user name", it)
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
class SharedGroupViewModel : ViewModel() {
    var currentGroupId by mutableStateOf<String?>(null)
        private set

    fun updateGroup(groupId: String) {
        currentGroupId = groupId
    }
}

class GroupViewModel : ViewModel() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth


    fun createGroup(
        name: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

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
                    ).await()
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

    var currentGroupId by mutableStateOf<String?>(null)
        private set

    fun selectGroup(groupId: String) {
        currentGroupId = groupId
    }

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

/**
 * for adding groceries
 */
data class GroceryItemDoc(
    val id: String = "",
    val name: String = "",
    val quantity: Long = 0L,
    val section: String = "toBuy",
    val addedBy: String = "",
    val updatedAt: Timestamp? = null
)

class GroupGroceryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _items = MutableStateFlow<List<GroceryItemDoc>>(emptyList())
    val items = _items.asStateFlow()

    private var groceryListener: ListenerRegistration? = null

    /**
     * Start listening to grocery items for a group (realtime)
     * Call this when the screen for `groupId` is shown.
     */
    fun listenToGroupGrocery(groupId: String) {
        groceryListener?.remove()
        groceryListener = db.collection("groups")
            .document(groupId)
            .collection("grocery")
            .orderBy("updatedAt")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e("GroceryVM", "listener error", err)
                    return@addSnapshotListener
                }
                val list = snap?.documents?.map { doc ->
                    GroceryItemDoc(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        quantity = doc.getLong("quantity") ?: 0L,
                        section = doc.getString("section") ?: "toBuy",
                        addedBy = doc.getString("addedBy") ?: "",
                        updatedAt = doc.getTimestamp("updatedAt")
                    )
                } ?: emptyList()
                _items.value = list
            }
    }

    fun stopListening() {
        groceryListener?.remove()
        groceryListener = null
    }

    /** Add item to a section ("toBuy" or "inventory") */
    fun addItem(groupId: String, name: String, qty: Long, section: String) {
        val uid = auth.currentUser?.uid ?: return
        val data = mapOf(
            "name" to name,
            "quantity" to qty,
            "section" to section,
            "addedBy" to uid,
            "updatedAt" to Timestamp.now()
        )
        db.collection("groups")
            .document(groupId)
            .collection("grocery")
            .add(data)
            .addOnFailureListener { Log.e("GroceryVM", "add failed", it) }
    }

    /** Move or update item quantity and/or section */
    fun updateItem(
        groupId: String,
        itemId: String,
        name: String? = null,
        qty: Long? = null,
        section: String? = null
    ) {
        val updates = mutableMapOf<String, Any>("updatedAt" to Timestamp.now())
        name?.let { updates["name"] = it }
        qty?.let { updates["quantity"] = it }
        section?.let { updates["section"] = it }

        db.collection("groups")
            .document(groupId)
            .collection("grocery")
            .document(itemId)
            .update(updates)
            .addOnFailureListener { Log.e("GroceryVM", "update failed", it) }
    }

    fun deleteItem(groupId: String, itemId: String) {
        db.collection("groups")
            .document(groupId)
            .collection("grocery")
            .document(itemId)
            .delete()
            .addOnFailureListener { Log.e("GroceryVM", "delete failed", it) }
    }

    override fun onCleared() {
        super.onCleared()
        groceryListener?.remove()
    }
}

/*
 * For adding chores
 */
data class ChoreItem(
    val id: String = "",
    val name: String = "",
    val assignedToName: String = "", // Storing name for simplicity in UI
    val assignedToId: String = "",
    val repeat: String = "Weekly",   // e.g. "Daily", "Weekly"
    val isDone: Boolean = false,
    val type: String = "Cleaning",   // cleaning, trash, laundry, etc.
    val updatedAt: Timestamp? = null
)

data class GroupMember(
    val uid: String,
    val name: String
)

class GroupChoreViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _chores = MutableStateFlow<List<ChoreItem>>(emptyList())
    val chores = _chores.asStateFlow()

    private val _members = MutableStateFlow<List<GroupMember>>(emptyList())
    val members = _members.asStateFlow()

    private var choreListener: ListenerRegistration? = null

    /** Listen to Chores Collection */
    fun listenToGroupChores(groupId: String) {
        choreListener?.remove()
        choreListener = db.collection("groups")
            .document(groupId)
            .collection("chores")
            .orderBy("updatedAt") // or order by isDone
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e("ChoreVM", "listener error", err)
                    return@addSnapshotListener
                }
                val list = snap?.documents?.map { doc ->
                    ChoreItem(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        assignedToName = doc.getString("assignedToName") ?: "Unassigned",
                        assignedToId = doc.getString("assignedToId") ?: "",
                        repeat = doc.getString("repeat") ?: "One-time",
                        isDone = doc.getBoolean("isDone") ?: false,
                        type = doc.getString("type") ?: "cleaning",
                        updatedAt = doc.getTimestamp("updatedAt")
                    )
                } ?: emptyList()
                _chores.value = list
            }

        fetchGroupMembers(groupId)
    }

    private fun fetchGroupMembers(groupId: String) {
        viewModelScope.launch {
            try {
                val groupDoc = db.collection("groups").document(groupId).get().await()
                val memberIds = groupDoc.get("members") as? List<String> ?: emptyList()

                if (memberIds.isNotEmpty()) {

                    val usersQuery = db.collection("users")
                        .whereIn(com.google.firebase.firestore.FieldPath.documentId(), memberIds)
                        .get()
                        .await()

                    val loadedMembers = usersQuery.documents.map { doc ->
                        GroupMember(
                            uid = doc.id,
                            name = doc.getString("name") ?: "Unknown"
                        )
                    }
                    _members.value = loadedMembers
                }
            } catch (e: Exception) {
                Log.e("ChoreVM", "Failed to fetch members", e)
            }
        }
    }

    fun addChore(groupId: String, name: String, assignee: GroupMember, repeat: String, type: String) {
        val data = mapOf(
            "name" to name,
            "assignedToName" to assignee.name,
            "assignedToId" to assignee.uid,
            "repeat" to repeat,
            "isDone" to false,
            "type" to type, // can expand later
            "updatedAt" to Timestamp.now()
        )
        db.collection("groups").document(groupId).collection("chores").add(data)
    }

    fun toggleChoreStatus(groupId: String, choreId: String, currentStatus: Boolean) {
        db.collection("groups").document(groupId).collection("chores").document(choreId)
            .update("isDone", !currentStatus)
    }

    fun deleteChore(groupId: String, choreId: String) {
        db.collection("groups").document(groupId).collection("chores").document(choreId)
            .delete()
    }

    fun stopListening() {
        choreListener?.remove()
    }
}

data class Message(
    val mid: String, //ID of message
    val toUid: String,
    val fromUid: String,
    val message: String
)



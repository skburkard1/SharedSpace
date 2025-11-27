package com.cs407.sharedspace.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cs407.sharedspace.data.GroupListViewModel
import com.cs407.sharedspace.data.GroupViewModel
import com.cs407.sharedspace.data.UserViewModel

/**
 * show groups user is in
 */
@Composable
fun MyGroupsScreen (
    viewModel : UserViewModel,
    groupListViewModel: GroupListViewModel,
    onGroupSelected: (String) -> Unit
) {
    val userState by viewModel.userState.collectAsState()
    val groups by groupListViewModel.groups.collectAsState()

    LaunchedEffect(userState.uid) {
        if (userState.uid.isNotEmpty()) {
            groupListViewModel.loadUserGroups()
        }
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ){
        Text("My Groups", modifier = Modifier.padding(16.dp))

    if (groups.isEmpty()) {
        Text(
            "You are not in any groups.",
            modifier = Modifier.padding(16.dp)
        )
    } else {

        groups.forEach { group ->
            val context = LocalContext.current
            var boool by remember { mutableStateOf(false) }
            Button(
                onClick = {

                    if(boool) boool = false
                    else{boool = true}
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                if(!boool) {
                    Text(group.name)
                }else{
                    Text("Invite Code: ${group.groupId}")

                }
            }
        }
    }
}
}

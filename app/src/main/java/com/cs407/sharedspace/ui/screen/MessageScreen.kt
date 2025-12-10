package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.sharedspace.data.Message
import com.cs407.sharedspace.data.MessagesViewModel
import com.cs407.sharedspace.ui.theme.PurpleGradientTop
import com.cs407.sharedspace.ui.theme.PurplePrimary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch


@Composable
fun MessageScreen(
    groupId: String,
    otherId: String,
    isGroup: Boolean,
    viewModel: MessagesViewModel,
    onBack: () -> Unit
){
    val uid = Firebase.auth.currentUser?.uid
    var chatId = ""

    // chat id of a group is just the group id
    // if its a dm, chat id is a combination of both uids
    if (uid != null) {
        if (isGroup) chatId = otherId
        else if (uid < otherId) chatId = uid + "_" + otherId
        else chatId = otherId + "_" + uid
    }



    val coroutineScope = rememberCoroutineScope()


    //Get messages from current and other user -> store in messages var
    val messages = viewModel.messages.collectAsState()
    val groupName = viewModel.groupName.collectAsState()
    val groupMembers = viewModel.members.collectAsState()
    val otherUserNames = viewModel.groupUserNames.collectAsState()

    var newMessageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = messages.value.size + 1) //scrolls to last item by default

    val context = LocalContext.current

    if (isGroup) {
        LaunchedEffect(chatId) {
            viewModel.listenToMessages(chatId, groupId)
        }
    } else {
        LaunchedEffect(chatId) {
            viewModel.listenToMessages(chatId, groupId, otherId)
        }
    }

    LaunchedEffect(messages.value) {
        listState.scrollToItem(messages.value.size + 1)
    }


    DisposableEffect(Unit) {
        onDispose { viewModel.stopListening() }
    }

    Scaffold(
        topBar = {
            Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
            }
            Text(
                text = if (isGroup) groupName.value else otherUserNames.value[otherId] ?: "",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }},
        bottomBar = {
            BottomAppBar(modifier = Modifier.imePadding()) {
                Row(modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(.85f),
                        value = newMessageText,
                        onValueChange = { newMessageText = it },
                    )
                    IconButton(onClick = {
                        val onComplete = {
                            newId: String ->
                            showToast(context, newId)
                        }
                        viewModel.sendMessage(chatId, Message(uid!!, newMessageText), onComplete)
                        coroutineScope.launch {
                            //listState.scrollToItem(messages.value.size + 1)
                        }
                        newMessageText = ""
                    }) {
                        Icon(Icons.AutoMirrored.Outlined.Send, "Send Message")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            state = listState
        ) {
            messages.value.forEach { message ->
                item() {
                    MessageBubble(uid == message.fromUid, message)
                }
            }
        }
    }

}

@Composable
fun MessageBubble(
    fromThis: Boolean, //true if from this user, false if from other user
    message: Message
){
    Box(
        Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .align(if (fromThis) Alignment.CenterEnd else Alignment.CenterStart )
                .padding(vertical = 4.dp)
                .requiredWidthIn(max = 300.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (fromThis) 16.dp else 0.dp,
                bottomEnd = if (fromThis) 0.dp else 16.dp
            ),
        ) {
            Box(modifier = Modifier
                .background(if (fromThis)  Brush.verticalGradient(
                    colors = listOf(
                        PurpleGradientTop,
                        PurplePrimary
                    )
                ) else Brush.verticalGradient(colors = listOf(Color.LightGray, Color.LightGray)))
                .padding(16.dp),) {
                Text(
                    message.messageText,
                    style = MaterialTheme.typography.titleLarge)
            }


        }
    }

}

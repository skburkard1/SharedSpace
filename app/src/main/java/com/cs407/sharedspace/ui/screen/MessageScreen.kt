package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.sharedspace.data.Message
import com.cs407.sharedspace.data.UserViewModel
import com.cs407.sharedspace.ui.theme.BgGray
import com.cs407.sharedspace.ui.theme.PurpleGradientTop
import com.cs407.sharedspace.ui.theme.PurplePrimary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun DirectMessageScreen(
    otherUserId: String,
    onBack: () -> Unit
){
    /*TODO: Get messages from current and other user -> store in messages var
        Note: you probably want to page these */
    val uid = Firebase.auth.currentUser?.uid

    var messages = listOf<Message>()

    //TODO: replace with messages from database
    if (uid != null) {
        messages = listOf(
            Message("1", uid, "", "Hello!"),
            Message("2", "", uid, "This is a test~!"),
            Message("3", "", uid, "a\nb\nc\nd\ne\naksldfjklakdjfalskdjflsakdjflkajsldfjsaalsdkfjasldkfjasldkjfasldjflakjdfljsdlksajdlfkjasdljsalfkdjsaldfkjasldkjasldkfjaslkdfjasld"),
            Message("4", "", uid, "SCROLL"),
            Message("5", "", uid, "SCROLLSCROLL"),
            Message("6", "", uid, "SCROLLSCROLL"),
            Message("7", "", uid, "SCROLLSCROLL"),
            Message("8", "", uid, "SCROLLSCROLL"),
            Message("9", "", uid, "SCROLLSCROLL"),
            Message("10", "", uid, "SCROLLSCROLL"),
            Message("11", "", uid, "SCROLLSCROLL")
        )
    }

    //TODO: get name of other user
    val otherUserName = "Roommate"
    MessageScreen(messages, otherUserName, uid, onBack)
}

@Composable
fun GroupMessageScreen(
    groupId: String,
    onBack: () -> Unit
) {
    /*TODO: Get messages from group -> store in messages var
        Note: you probably want to page these */
    val uid = Firebase.auth.currentUser?.uid

    var messages = listOf<Message>()

    //TODO: replace with messages from database
    if (uid != null) {
        messages = listOf(
            Message("1", uid, "", "Hello!"),
            Message("2", "", uid, "This is a test~!"),
            Message("3", "", uid, "a\nb\nc\nd\ne\naksldfjklakdjfalskdjflsakdjflkajsldfjsaalsdkfjasldkfjasldkjfasldjflakjdfljsdlksajdlfkjasdljsalfkdjsaldfkjasldkjasldkfjaslkdfjasld"),
            Message("4", "", uid, "SCROLL"),
            Message("5", "", uid, "SCROLLSCROLL"),
            Message("6", "", uid, "SCROLLSCROLL"),
            Message("7", "", uid, "SCROLLSCROLL"),
            Message("8", "", uid, "SCROLLSCROLL"),
            Message("9", "", uid, "SCROLLSCROLL"),
            Message("10", "", uid, "SCROLLSCROLL"),
            Message("11", "", uid, "SCROLLSCROLL")
        )
    }

    //TODO: get name of group
    val groupName = "group"
    MessageScreen(messages, groupName, uid, onBack)
}


@Composable
fun MessageScreen(
    messages: List<Message>,
    chatName: String,
    uid: String?,
    onBack: () -> Unit
){


    //TODO: Consider adding real time check for new messages. Or message notifications?
    var newMessageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = messages.size) //scrolls to last item by default

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
                text = chatName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }},
        bottomBar = {
            BottomAppBar {
                Row(modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(.85f),
                        value = newMessageText,
                        onValueChange = { newMessageText = it },
                    )
                    IconButton({
                        //TODO: Send Message, add to database
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
            messages.forEach { message ->
                item(message.mid) {
                    MessageBubble(uid == message.fromUid, message.message)
                }
            }
        }
    }

}

@Composable
fun MessageBubble(
    fromThis: Boolean, //true if from this user, false if from other user
    messageText: String
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
                    messageText,
                    style = MaterialTheme.typography.titleLarge)
            }


        }
    }

}

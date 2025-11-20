package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.sharedspace.R
import com.cs407.sharedspace.data.Chore
import com.cs407.sharedspace.data.ChoreImage
import com.cs407.sharedspace.data.ChoreRepeats

@Composable
fun ChoreCard( //TODO: Replace with corresponding fields in Chore Database
    chore: Chore,
    onChecked: () -> Unit,
    onEdit: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //TODO: Change Image based on Image in Database
                Image(painterResource(chore.choreImage.id), chore.choreImage.contentDescription, modifier = Modifier.size(64.dp).padding(4.dp))
                Column(modifier = Modifier) {
                    Text(chore.choreName, fontWeight = FontWeight.Bold)
                    Text(stringResource(id = R.string.chore_repeats) + " " + chore.choreRepeats.repeatName)
                    //TODO: Check if choreAssignee id == UserId and change text accordingly
                    Text(stringResource(id = R.string.chore_assigned_to) + " " + chore.choreAssignee)
                }
            }
            Row(modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = chore.choreTicked,
                    onCheckedChange = { onChecked }
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, "Edit Chore")
                }
            }

        }
    }
}

@Composable
fun ChoreScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(32.dp)) // put app name closer in line with other screens
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
            }
            // App title
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(24.dp)) // for correct spacing
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            item {
                //TODO: Take chores from Database
                ChoreCard(
                    Chore(
                        1,
                        "Laundry",
                        1,
                        "John",
                        ChoreRepeats.TWICE_WEEKLY,
                        ChoreImage.CLEANING,
                        true
                    ), {}, {})
            }
        }
    }
}
package com.suonk.notepad_plus.designsystem.note_details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarDetails(
    onDeleteRestoreNoteClicked: () -> Unit,
    onBackToListClicked: () -> Unit,
    onSaveNoteClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text("", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = { onBackToListClicked() }) {
//                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back_arrow))
            }
        },
        actions = {
            IconButton(onClick = {
                onDeleteRestoreNoteClicked()
            }) {
//                Icon(
//                    imageVector = ImageVector.vectorResource(id = if (isDeleted) R.drawable.ic_restore else R.drawable.ic_garbage),
//                    contentDescription = stringResource(R.string.toolbar_delete)
//                )
            }
            IconButton(onClick = {
                onSaveNoteClicked()
            }) {
//                Icon(
//                    imageVector = Icons.Default.Create, contentDescription = stringResource(R.string.toolbar_save)
//                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

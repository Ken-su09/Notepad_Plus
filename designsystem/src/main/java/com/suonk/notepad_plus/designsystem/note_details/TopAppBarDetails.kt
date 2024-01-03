package com.suonk.notepad_plus.designsystem.note_details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import com.suonk.notepad_plus.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarDetails(
    onDeleteNoteClicked: () -> Unit,
    onDefinitiveDeleteNoteClicked: () -> Unit,
    onRestoreNoteClicked: () -> Unit,
    onBackToListClicked: () -> Unit,
    onSaveNoteClicked: () -> Unit,
    isDeleted: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val isAlertDialogVisible = rememberSaveable { mutableStateOf(false) }

    if (isAlertDialogVisible.value) {
        AlertDialogExample(onBackToListClicked, onDefinitiveDeleteNoteClicked, isAlertDialogVisible)
    }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text("", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = { onBackToListClicked() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back_arrow))
            }
        },
        actions = {
            if (isDeleted) {
                IconButton(onClick = {
                    onRestoreNoteClicked()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_restore),
                        contentDescription = stringResource(R.string.toolbar_restore)
                    )
                }
                IconButton(onClick = {
                    isAlertDialogVisible.value = !isAlertDialogVisible.value
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_garbage),
                        contentDescription = stringResource(R.string.toolbar_delete)
                    )
                }
            } else {
                IconButton(onClick = {
                    onDeleteNoteClicked()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_garbage),
                        contentDescription = stringResource(R.string.toolbar_delete)
                    )
                }
            }
            IconButton(onClick = {
                onSaveNoteClicked()
            }) {
                Icon(
                    imageVector = Icons.Default.Create, contentDescription = stringResource(R.string.toolbar_save)
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(onBackToListClicked: () -> Unit, onDefinitiveDeleteNoteClicked: () -> Unit, isAlertDialogVisible: MutableState<Boolean>) {
    AlertDialog(icon = {
        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_garbage), contentDescription = "Example Icon")
    }, title = {
        Text(text = stringResource(R.string.delete_note_alert_dialog_title))
    }, text = {
        Text(text = stringResource(R.string.delete_note_alert_dialog_message))
    }, onDismissRequest = {
        isAlertDialogVisible.value = !isAlertDialogVisible.value
    }, confirmButton = {
        TextButton(onClick = {
            isAlertDialogVisible.value = !isAlertDialogVisible.value
            onDefinitiveDeleteNoteClicked()
            onBackToListClicked()
        }) {
            Text(stringResource(R.string.alert_dialog_confirm))
        }
    }, dismissButton = {
        TextButton(onClick = {
            isAlertDialogVisible.value = !isAlertDialogVisible.value
        }) {
            Text(stringResource(R.string.alert_dialog_dismiss))
        }
    })
}

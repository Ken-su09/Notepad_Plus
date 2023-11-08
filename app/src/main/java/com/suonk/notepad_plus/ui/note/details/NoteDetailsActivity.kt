package com.suonk.notepad_plus.ui.note.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcomposetutorial.ui.theme.NotepadPlusTheme
import com.suonk.notepad_plus.ui.note.list.NotesListActivity
import com.suonk.notepad_plus.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotepadPlusTheme {
                AppPortrait(this@NoteDetailsActivity, {
                    startActivity(Intent(this@NoteDetailsActivity, NotesListActivity::class.java))
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppPortrait(context: Context, onBackIconClicked: () -> Unit, viewModel: NoteDetailsViewModel = viewModel()) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val coroutineScope = rememberCoroutineScope()

    val noteDetails by viewModel.noteDetailsFlow.collectAsState()

    LaunchedEffect(coroutineScope) {
        viewModel.noteDetailsUiEvent.collectLatest { noteDetailsUiEvent ->
            when (noteDetailsUiEvent) {
                is NoteDetailsUiEvent.ShowToast -> {
                    noteDetailsUiEvent.message.showToast(context)
                }
                is NoteDetailsUiEvent.ActionFinish -> {
                    onBackIconClicked()
                }

                else -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "", maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.setNoteIdToNull()
                        onBackIconClicked()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack, contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onDeleteNoteMenuItemClicked() }) {
                        Icon(
                            imageVector = Icons.Filled.Delete, contentDescription = "Delete note"
                        )
                    }
                    IconButton(onClick = {
                        viewModel.onEvent(NoteDetailsDataEvent.SaveDataNote(noteDetails.title, noteDetails.content, noteDetails.color))
//                        viewModel.onSaveNoteMenuItemClicked(noteDetails.title, noteDetails.content, noteDetails.color)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Create, contentDescription = "Save note"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        EntireLayout(innerPadding, noteDetails, viewModel.listOfColors())
    }
}

@Composable
private fun EntireLayout(padding: PaddingValues, noteDetails: NoteDetailsViewState, listOfColors: List<Long>) {
    val noteBackgroundAnimatable = remember {
        Animatable(
            Color(noteDetails.color)
        )
    }

    val stableNoteDetails by rememberUpdatedState(noteDetails)
    Log.i("GetNoteDetails", "noteDetails : $stableNoteDetails")

    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(noteBackgroundAnimatable.value)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOfColors.forEach { color ->
                Box(modifier = Modifier
                    .size(50.dp)
                    .shadow(15.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(color))
                    .border(
                        width = 3.dp, color = if (noteDetails.color == color) {
                            Color.Black
                        } else Color.Transparent, shape = CircleShape
                    )
                    .clickable {
                        scope.launch {
//                                viewModel.onColorSelected(color)
                            noteBackgroundAnimatable.animateTo(
                                targetValue = Color(color), animationSpec = tween(
                                    durationMillis = 500
                                )
                            )
                        }
//                            viewModel.onEvent(NoteDetailsEvent.ChangeColor(color))
                    })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(16.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFe7dfec), Color(0xFFe7dfec))),
                    shape = RoundedCornerShape(60.dp),
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Log.i("GetNoteDetails", "noteDetails.title : ${noteDetails.title}")

            BasicTextField(
                value = noteDetails.title, maxLines = 2, onValueChange = { newText ->
//                    titleState.value = newText
                }, textStyle = TextStyle(color = Color.Black, fontSize = 18.sp), modifier = Modifier.padding(start = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFe7dfec), Color(0xFFe7dfec))),
                    shape = RoundedCornerShape(60.dp),
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Log.i("GetNoteDetails", "noteDetails.content : ${noteDetails.content}")

            BasicTextField(
                value = noteDetails.content, onValueChange = { newText ->
//                    contentState.value = newText
                }, textStyle = TextStyle(color = Color.Black, fontSize = 16.sp), modifier = Modifier.padding(16.dp)
            )
        }
    }
}
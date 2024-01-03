package com.suonk.notepad_plus.ui.note.details

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcomposetutorial.ui.theme.NotepadPlusTheme
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.designsystem.note_details.TopAppBarDetails
import com.suonk.notepad_plus.designsystem.utils.toARGB
import com.suonk.notepad_plus.ui.note.deleted_list.DeletedNotesListActivity
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
                val viewModel: NoteDetailsViewModel = viewModel()
                val isDeleted by viewModel.isDeleted.collectAsState()
                AppPortrait({
                    viewModel.setNoteIdToNull()
                    if (isDeleted) {
                        startActivity(Intent(this@NoteDetailsActivity, DeletedNotesListActivity::class.java))
                    } else {
                        startActivity(Intent(this@NoteDetailsActivity, NotesListActivity::class.java))
                    }
                }, {
                    viewModel.onDataEvent(NoteDetailsDataEvent.DeleteNote)
                }, {
                    viewModel.onDataEvent(NoteDetailsDataEvent.DefinitiveDeleteNote)
                }, {
                    viewModel.onDataEvent(NoteDetailsDataEvent.RestoreNote)
                }, {
                    viewModel.onDataEvent(NoteDetailsDataEvent.SaveNote)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppPortrait(
    onBackToListClicked: () -> Unit,
    onDeleteNoteClicked: () -> Unit,
    onDefinitiveDeleteNoteClicked: () -> Unit,
    onRestoreNoteClicked: () -> Unit,
    onSaveNoteClicked: () -> Unit,
    viewModel: NoteDetailsViewModel = viewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isDeleted by viewModel.isDeleted.collectAsState()

    LaunchedEffect(coroutineScope) {
        viewModel.noteDetailsUiEvent.collectLatest { noteDetailsUiEvent ->
            when (noteDetailsUiEvent) {
                is NoteDetailsUiEvent.ShowToast -> {
                    noteDetailsUiEvent.message.showToast(context)
                }

                is NoteDetailsUiEvent.ActionFinish -> {
                    onBackToListClicked()
                }
            }
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarDetails(
                onDeleteNoteClicked = onDeleteNoteClicked,
                onDefinitiveDeleteNoteClicked = onDefinitiveDeleteNoteClicked,
                onRestoreNoteClicked = onRestoreNoteClicked,
                onBackToListClicked = onBackToListClicked,
                onSaveNoteClicked = onSaveNoteClicked,
                isDeleted = isDeleted,
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        EntireLayout(innerPadding, viewModel, isDeleted)
    }
}

@Composable
private fun EntireLayout(
    padding: PaddingValues, viewModel: NoteDetailsViewModel, isEnabled: Boolean
) {
    val colorState by viewModel.noteColor.collectAsState()
    val noteAllColors by viewModel.noteAllColors.collectAsState()

    val noteBackgroundAnimatable = remember { Animatable(Color(colorState.toARGB())) }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(colorState.toARGB()))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            noteAllColors.forEach { color ->
                Box(modifier = Modifier
                    .size(35.dp)
                    .shadow(15.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(color.toARGB()))
                    .border(
                        width = 3.dp, color = if (colorState == color) {
                            Color.Black
                        } else Color.Transparent, shape = CircleShape
                    )
                    .clickable(enabled = !isEnabled) {
                        scope.launch {
                            noteBackgroundAnimatable.animateTo(
                                targetValue = Color(color.toARGB()), animationSpec = tween(
                                    durationMillis = 500
                                )
                            )
                        }
                        viewModel.onDataEvent(NoteDetailsDataEvent.ChangeColor(color))
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
            val titleState by viewModel.noteTitle.collectAsState()

            BasicTextField(
                value = titleState.text, maxLines = 2, onValueChange = { newTitle ->
                    viewModel.onDataEvent(NoteDetailsDataEvent.ChangeTitle(newTitle))
                }, textStyle = TextStyle(color = Color.Black, fontSize = 18.sp), enabled = !isEnabled, modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
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
            val contentState by viewModel.noteContent.collectAsState()
            BasicTextField(
                value = contentState.text, onValueChange = { newContent ->
                    viewModel.onDataEvent(NoteDetailsDataEvent.ChangeContent(newContent))
                }, textStyle = TextStyle(color = Color.Black, fontSize = 16.sp), enabled = !isEnabled, modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorControls(
    modifier: Modifier = Modifier,
    state: RichTextState,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onUnderlineClick: () -> Unit,
    onTitleClick: () -> Unit,
    onSubtitleClick: () -> Unit,
    onTextColorClick: () -> Unit,
    onStartAlignClick: () -> Unit,
    onEndAlignClick: () -> Unit,
    onCenterAlignClick: () -> Unit,
    onExportClick: () -> Unit,
) {
    var boldSelected by rememberSaveable { mutableStateOf(false) }
    var italicSelected by rememberSaveable { mutableStateOf(false) }
    var underlineSelected by rememberSaveable { mutableStateOf(false) }
    var titleSelected by rememberSaveable { mutableStateOf(false) }
    var subtitleSelected by rememberSaveable { mutableStateOf(false) }
    var textColorSelected by rememberSaveable { mutableStateOf(false) }
    var linkSelected by rememberSaveable { mutableStateOf(false) }
    var alignmentSelected by rememberSaveable { mutableIntStateOf(0) }

    var showLinkDialog by remember { mutableStateOf(false) }

//    AnimatedVisibility(visible = showLinkDialog) {
//        LinkDialog(
//            onDismissRequest = {
//                showLinkDialog = false
//                linkSelected = false
//            },
//            onConfirmation = { linkText, link ->
//                state.addLink(
//                    text = linkText,
//                    url = link
//                )
//                showLinkDialog = false
//                linkSelected = false
//            }
//        )
//    }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val globalModifier = Modifier.size(15.dp)
        ControlWrapper(
            selected = false, onChangeClick = { }, onClick = onUndoClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_undo),
                contentDescription = "Undo Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = false, onChangeClick = { }, onClick = onRedoClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_redo),
                contentDescription = "Redo Control",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = globalModifier
            )
        }
        ControlWrapper(
            selected = boldSelected, onChangeClick = { boldSelected = it }, onClick = onBoldClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_bold),
                contentDescription = "Bold Control",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = globalModifier
            )
        }
        ControlWrapper(
            selected = italicSelected, onChangeClick = { italicSelected = it }, onClick = onItalicClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_italic),
                contentDescription = "Italic Control",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = globalModifier
            )
        }
        ControlWrapper(
            selected = underlineSelected, onChangeClick = { underlineSelected = it }, onClick = onUnderlineClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_underlined),
                contentDescription = "Underline Control",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = globalModifier
            )
        }
        ControlWrapper(
            selected = titleSelected, onChangeClick = { titleSelected = it }, onClick = onTitleClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_restore),
                contentDescription = "Title Control",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = globalModifier
            )
        }
        ControlWrapper(
            selected = subtitleSelected, onChangeClick = { subtitleSelected = it }, onClick = onSubtitleClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_text_color),
                contentDescription = "Subtitle Control",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = globalModifier
            )
        }
        ControlWrapper(
            selected = textColorSelected, onChangeClick = { textColorSelected = it }, onClick = onTextColorClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_text_color),
                contentDescription = "Text Color Control",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = globalModifier
            )
        }
        ControlWrapper(selected = linkSelected, onChangeClick = { linkSelected = it }, onClick = { showLinkDialog = true }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_link),
                contentDescription = "Link Control",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = globalModifier
            )
        }
        ControlWrapper(
            selected = alignmentSelected == 0, onChangeClick = { alignmentSelected = 0 }, onClick = onStartAlignClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_align_left),
                contentDescription = "Start Align Control",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = globalModifier
            )
        }
        ControlWrapper(
            selected = alignmentSelected == 1, onChangeClick = { alignmentSelected = 1 }, onClick = onCenterAlignClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_align_center),
                contentDescription = "Center Align Control",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = globalModifier
            )
        }
        ControlWrapper(
            selected = alignmentSelected == 2, onChangeClick = { alignmentSelected = 2 }, onClick = onEndAlignClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_align_right),
                contentDescription = "End Align Control",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = globalModifier
            )
        }
//        ControlWrapper(
//            selected = true,
//            selectedColor = MaterialTheme.colorScheme.tertiary,
//            onChangeClick = { },
//            onClick = onExportClick
//        ) {
//            Icon(
//                imageVector = Icons.Default.Save,
//                contentDescription = "Export Control",
//                tint = MaterialTheme.colorScheme.onPrimary
//            )
//        }
    }
}

@Composable
fun ControlWrapper(
    selected: Boolean,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.inversePrimary,
    onChangeClick: (Boolean) -> Unit,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(size = 6.dp))
        .clickable {
            onClick()
            onChangeClick(!selected)
        }
        .background(
            if (selected) selectedColor
            else unselectedColor
        )
        .border(
            width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(size = 6.dp)
        )
        .padding(all = 8.dp), contentAlignment = Alignment.Center) {
        content()
    }
}
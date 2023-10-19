package com.suonk.notepad_plus.ui.note.details

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcomposetutorial.ui.theme.NotepadPlusTheme
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.suonk.notepad_plus.ui.note.list.NoteListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotepadPlusTheme {
                AppPortrait({
                    startActivity(Intent(this@NoteDetailsActivity, NoteListActivity::class.java))
                })
            }
        }
    }
}

//region ================================================================ SEARCH BAR ================================================================

//endregion

@Composable
private fun TitleEditText(title: String) {

}

@Composable
private fun EntireLayout(modifier: Modifier = Modifier, viewModel: NoteDetailsViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
//        MainScreen()

        val noteDetails by viewModel.noteDetailsFlow.collectAsState()

        BasicTextField(
            value = noteDetails.title, maxLines = 2,
            onValueChange = { newText ->
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(top = 100.dp, start = 16.dp),
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp)
        )

        val state = rememberRichTextState()
        state.setText(noteDetails.content)
        RichTextEditor(
            state = state, modifier = Modifier
                .height(300.dp)
                .padding(top = 150.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppPortrait(onFloatingButtonClick: () -> Unit, viewModel: NoteDetailsViewModel = viewModel()) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
                        "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onFloatingButtonClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onDeleteNoteMenuItemClicked() }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete note"
                        )
                    }
                    IconButton(onClick = { viewModel.updateEditTextContent("") }) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Save note"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        EntireLayout(Modifier.padding(innerPadding), viewModel)
    }
}

@Composable
fun MainScreen() {
    val state = rememberRichTextState()
    val titleSize = MaterialTheme.typography.displaySmall.fontSize
    val subtitleSize = MaterialTheme.typography.titleLarge.fontSize

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 20.dp)
                .padding(bottom = it.calculateBottomPadding())
                .padding(top = it.calculateTopPadding()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EditorControls(modifier = Modifier.weight(2f), state = state, onBoldClick = {
                state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
            }, onItalicClick = {
                state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
            }, onUnderlineClick = {
                state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            }, onTitleClick = {
                state.toggleSpanStyle(SpanStyle(fontSize = titleSize))
            }, onSubtitleClick = {
                state.toggleSpanStyle(SpanStyle(fontSize = subtitleSize))
            }, onTextColorClick = {
                state.toggleSpanStyle(SpanStyle(color = Color.Red))
            }, onStartAlignClick = {
//                    state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Start))
            }, onEndAlignClick = {
//                    state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.End))
            }, onCenterAlignClick = {
//                    state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Center))
            }, onExportClick = {
//                    Log.d("Editor", state.toHtml())
            })
//            RichTextEditor(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(8f),
//                state = state,
//            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorControls(
    modifier: Modifier = Modifier,
    state: RichTextState,
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

    AnimatedVisibility(visible = showLinkDialog) {
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
    }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ControlWrapper(
            selected = boldSelected, onChangeClick = { boldSelected = it }, onClick = onBoldClick
        ) {
            Icon(
                imageVector = Icons.Default.Build, contentDescription = "Bold Control", tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = italicSelected, onChangeClick = { italicSelected = it }, onClick = onItalicClick
        ) {
            Icon(
                imageVector = Icons.Default.Face, contentDescription = "Italic Control", tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = underlineSelected, onChangeClick = { underlineSelected = it }, onClick = onUnderlineClick
        ) {
            Icon(
                imageVector = Icons.Default.Menu, contentDescription = "Underline Control", tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = titleSelected, onChangeClick = { titleSelected = it }, onClick = onTitleClick
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward, contentDescription = "Title Control", tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = subtitleSelected, onChangeClick = { subtitleSelected = it }, onClick = onSubtitleClick
        ) {
            Icon(
                imageVector = Icons.Default.Refresh, contentDescription = "Subtitle Control", tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = textColorSelected, onChangeClick = { textColorSelected = it }, onClick = onTextColorClick
        ) {
            Icon(
                imageVector = Icons.Default.Warning, contentDescription = "Text Color Control", tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(selected = linkSelected, onChangeClick = { linkSelected = it }, onClick = { showLinkDialog = true }) {
            Icon(
                imageVector = Icons.Default.Favorite, contentDescription = "Link Control", tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = alignmentSelected == 0, onChangeClick = { alignmentSelected = 0 }, onClick = onStartAlignClick
        ) {
            Icon(
                imageVector = Icons.Default.Home, contentDescription = "Start Align Control", tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = alignmentSelected == 1, onChangeClick = { alignmentSelected = 1 }, onClick = onCenterAlignClick
        ) {
            Icon(
                imageVector = Icons.Default.Home, contentDescription = "Center Align Control", tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = alignmentSelected == 2, onChangeClick = { alignmentSelected = 2 }, onClick = onEndAlignClick
        ) {
            Icon(
                imageVector = Icons.Default.Home, contentDescription = "End Align Control", tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = true, selectedColor = MaterialTheme.colorScheme.tertiary, onChangeClick = { }, onClick = onExportClick
        ) {
            Icon(
                imageVector = Icons.Default.Home, contentDescription = "Export Control", tint = MaterialTheme.colorScheme.onPrimary
            )
        }
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

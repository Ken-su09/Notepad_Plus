package com.suonk.notepad_plus.ui.note.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcomposetutorial.ui.theme.NotepadPlusTheme
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.ui.note.deleted_list.DeletedNotesListActivity
import com.suonk.notepad_plus.ui.note.details.NoteDetailsActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotesListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotepadPlusTheme {
                AppPortrait({
                    startActivity(Intent(this@NotesListActivity, NoteDetailsActivity::class.java))
                }, {
                    startActivity(Intent(this@NotesListActivity, NoteDetailsActivity::class.java))
                }, {
                    startActivity(Intent(this@NotesListActivity, DeletedNotesListActivity::class.java))
                })
            }
        }
    }
}

//region ===================================================================== SEARCH BAR =====================================================================

@Composable
private fun SearchBar(modifier: Modifier = Modifier, viewModel: NotesListViewModel) {
    val searchBarText by viewModel.searchBarText.collectAsState()

    TextField(value = searchBarText, label = { Text("Search Note") }, onValueChange = { searchText ->
        viewModel.setSearchParameters(searchText)
    }, leadingIcon = {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
    }, textStyle = TextStyle(color = Color(0xFF484644)), modifier = modifier
    )
}

//endregion

//region ===================================================================== NOTES LIST =====================================================================

@Composable
private fun ListOfNotes(modifier: Modifier, list: List<NotesListViewState>, onItemNoteClicked: () -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = list) { item ->
            NoteLayout(modifier, item, onItemNoteClicked, onItemNoteClicked)
        }
    }
}

@Composable
private fun NoteLayout(
    modifier: Modifier,
    note: NotesListViewState,
    onItemNoteClicked: () -> Unit,
    onDeleteNoteClicked: () -> Unit,
    cornerRadius: Dp = 10.dp,
    cutCornerSize: Dp = 30.dp
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val clipPath = Path().apply {
                lineTo(size.width - cutCornerSize.toPx(), 0f)
                lineTo(size.width, cutCornerSize.toPx())
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            clipPath(clipPath) {
                drawRoundRect(
                    color = Color(note.color), size = size, cornerRadius = CornerRadius(cornerRadius.toPx())
                )
                val originalColor = Color(note.color)
                val darkerColor = makeColorDarker(originalColor, 0.8F)
                drawRoundRect(
                    color = darkerColor,
                    topLeft = Offset(size.width - cutCornerSize.toPx(), -100f),
                    size = Size(cutCornerSize.toPx() + 100f, cutCornerSize.toPx() + 100f),
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(end = 32.dp)
                .clickable {
                    note.onItemNoteClicked()
                    onItemNoteClicked()
                }
        ) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = note.date,
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { note.onDeleteNoteClicked.invoke() }, modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete note"
                )
            }
        }
    }
}

//endregion

@Composable
private fun EntireLayout(
    modifier: Modifier = Modifier,
    onAddNewNoteClicked: () -> Unit,
    onItemNoteClicked: () -> Unit,
    viewModel: NotesListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column() {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .heightIn(max = 56.dp), viewModel
            )
            val listOfNotes by viewModel.notesListFlow.collectAsState()
            ListOfNotes(modifier, listOfNotes, onItemNoteClicked)
        }
    }
}

@Composable
private fun HorizontalBottomNavigationView(onDeleteBottomNavClicked: () -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_notes),
                    contentDescription = stringResource(R.string.nav_notes)
                )
            },
            selected = true,
            onClick = {

            },
            colors = androidx.compose.material3.NavigationBarItemDefaults
                .colors(
                    selectedIconColor = Color.Magenta,
                    selectedTextColor = Color.Magenta,
                ),
            label = {
                Text(text = stringResource(R.string.nav_notes))
            })
        NavigationBarItem(icon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_garbage),
                contentDescription = stringResource(R.string.nav_garbage)
            )
        },
            selected = false,
            onClick = {
                onDeleteBottomNavClicked()
            },
            label = {
                Text(text = stringResource(R.string.nav_garbage))
            })
    }
}

@Composable
private fun AppPortrait(onAddNewNoteClicked: () -> Unit, onItemNoteClicked: () -> Unit, onDeleteBottomNavClicked: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNewNoteClicked,
                modifier = Modifier.padding(bottom = 30.dp, end = 10.dp),
                contentColor = Color.Blue
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.a11y_add_note))
            }
        },
        bottomBar = { HorizontalBottomNavigationView(onDeleteBottomNavClicked) }) { padding ->
        EntireLayout(Modifier.padding(padding), onAddNewNoteClicked, onItemNoteClicked)
    }
}

fun makeColorDarker(color: Color, scaleFactor: Float): Color {
    return Color(
        red = color.red * scaleFactor,
        green = color.green * scaleFactor,
        blue = color.blue * scaleFactor,
        alpha = color.alpha
    )
}
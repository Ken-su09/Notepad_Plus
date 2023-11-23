package com.suonk.notepad_plus.ui.note.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.suonk.notepad_plus.design_system.notes_dropdown_menuitem.NotesDropdownMenuItem
import com.suonk.notepad_plus.design_system.notes_dropdown_menuitem.NotesDropdownMenuItemViewState
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

    TextField(
        value = searchBarText,
        label = { Text("Search Note") },
        onValueChange = { searchText ->
            viewModel.setSearchParameters(searchText)
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
        },
        textStyle = TextStyle(color = Color(0xFF484644)),
        modifier = modifier
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
        Column {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppPortrait(
    onAddNewNoteClicked: () -> Unit, onItemNoteClicked: () -> Unit, onDeleteBottomNavClicked: () -> Unit,
    viewModel: NotesListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var sortMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var sortFilterExpanded by rememberSaveable { mutableStateOf(false) }

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
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    SearchBar(
                        modifier = Modifier
                            .weight(2f)
                            .padding(16.dp)
                            .heightIn(max = 56.dp),
                        viewModel
                    )
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = 56.dp)
                    ) {
                        IconButton(
                            onClick = { sortMenuExpanded = !sortMenuExpanded }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_sort),
                                contentDescription = stringResource(R.string.toolbar_sort)
                            )
                        }
                        IconButton(onClick = { sortFilterExpanded = !sortFilterExpanded }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_filter),
                                contentDescription = stringResource(R.string.toolbar_filter),
                            )
                        }
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            val actions = mutableListOf<NotesDropdownMenuItemViewState>()

                            actions.forEach { action ->
                                NotesDropdownMenuItem(actions)
                            }


                            DropdownMenuItem(text = { Text(text = stringResource(R.string.date_desc)) }, onClick = {
                                sortMenuExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.date_desc)
                            })
                            Divider()
                            DropdownMenuItem(text = { Text(text = stringResource(R.string.title_asc)) }, onClick = {
                                sortMenuExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.title_asc)
                            })
                            DropdownMenuItem(text = { Text(text = stringResource(R.string.title_desc)) }, onClick = {
                                sortMenuExpanded = false
                                Log.i("SortNote", "activity R.string.date_asc : ${R.string.title_desc}")
                                viewModel.setCurrentSortFilterParameters(R.string.title_desc)
                            })
                            DropdownMenuItem(text = { Text(text = stringResource(R.string.content_a_z)) }, onClick = {
                                sortMenuExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.content_a_z)
                            })
                            DropdownMenuItem(text = { Text(text = stringResource(R.string.content_z_a)) }, onClick = {
                                sortMenuExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.content_z_a)
                            })
                            DropdownMenuItem(text = { Text(text = stringResource(R.string.by_color)) }, onClick = {
                                sortMenuExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.by_color)
                            })
                        }
                        DropdownMenu(
                            expanded = sortFilterExpanded,
                            onDismissRequest = { sortFilterExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {

                            DropdownMenuItem(text = { Text(text = stringResource(R.string.remove_filter)) }, onClick = {
                                sortFilterExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.remove_filter)
                            })
                            DropdownMenuItem(text = { Text(text = stringResource(R.string.pink)) }, onClick = {
                                sortFilterExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.pink)
                            })
                            DropdownMenuItem(text = { Text(text = stringResource(R.string.green)) }, onClick = {
                                sortFilterExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.green)
                            })
                            DropdownMenuItem(text = { Text(text = stringResource(R.string.orange)) }, onClick = {
                                sortFilterExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.orange)
                            })
                            DropdownMenuItem(text = { Text(text = stringResource(R.string.yellow)) }, onClick = {
                                sortFilterExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.yellow)
                            })
                            DropdownMenuItem(text = { Text(text = stringResource(R.string.purple)) }, onClick = {
                                sortFilterExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.purple)
                            })
                            DropdownMenuItem(text = { Text(text = stringResource(R.string.blue)) }, onClick = {
                                sortFilterExpanded = false
                                viewModel.setCurrentSortFilterParameters(R.string.blue)
                            })
                        }
                    }

                }
            )
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

//@Composable
//fun CheckableDropdownMenuItem(
//    text: String,
//    checked: Boolean,
//    onCheckedChange: (Boolean) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    DropdownMenuItem(
//        onClick = {
//            onCheckedChange(!checked)
//        },
//        modifier = modifier
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(text = text)
//            Checkbox(
//                checked = checked,
//                onCheckedChange = null // Disable Checkbox interaction
//            )
//        }
//    }
//}
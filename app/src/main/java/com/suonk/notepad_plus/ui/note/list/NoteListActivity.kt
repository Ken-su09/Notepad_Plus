package com.suonk.notepad_plus.ui.note.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcomposetutorial.ui.theme.NotepadPlusTheme
import com.suonk.notepad_plus.ui.note.details.NoteDetailsActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NoteListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotepadPlusTheme {
                AppPortrait { startActivity(Intent(this@NoteListActivity, NoteDetailsActivity::class.java)) }
            }
        }
    }
}

//region ================================================================ SEARCH BAR ================================================================

@Composable
private fun SearchBar(modifier: Modifier = Modifier, viewModel: NotesListViewModel) {
    TextField(value = "Search", onValueChange = { searchText ->
        viewModel.setSearchParameters(searchText)
    }, leadingIcon = {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
    }, textStyle = TextStyle(color = Color(0xFF484644)), modifier = modifier
    )
}

//endregion

//region =========================================================== NOTES COLLECTIONS ===========================================================

@Composable
private fun ListOfNotes(list: List<NotesListViewState>, onFloatingButtonClick: () -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
//        modifier = Modifier.height(230.dp)
    ) {
        items(items = list) { item ->
            NoteLayout(item, onFloatingButtonClick)
        }
    }
}

@Composable
private fun NoteLayout(note: NotesListViewState, onFloatingButtonClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFFe5e0dd),
        modifier = Modifier.padding(16.dp).clickable(enabled= true, onClickLabel = null, role = null, onClick = onFloatingButtonClick) ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 3,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = note.content,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                maxLines = 3,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = note.date,
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }
}

//endregion

@Composable
private fun EntireLayout(
    modifier: Modifier = Modifier,
    onFloatingButtonClick: () -> Unit,
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
            ListOfNotes(listOfNotes, onFloatingButtonClick)
        }

        FloatingActionButton(
            onClick = onFloatingButtonClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 25.dp),
            contentColor = Color.Blue
        ) {
            Icon(Icons.Filled.Add, "")
        }
    }
}

@Composable
private fun HorizontalBottomNavigationView() {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        NavigationBarItem(icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") }, selected = true, onClick = { })
        NavigationBarItem(icon = { Icon(imageVector = Icons.Default.AccountBox, contentDescription = "Account") },
            selected = true,
            onClick = { })
    }
}

@Composable
private fun AppPortrait(onFloatingButtonClick: () -> Unit) {
    Scaffold(bottomBar = { HorizontalBottomNavigationView() }) { padding ->
        EntireLayout(Modifier.padding(padding), onFloatingButtonClick)
    }
}

//@Preview
//@Composable
//fun EntireLayoutPreview(modifier: Modifier = Modifier, viewModel: NotesListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
//    NotepadPlusTheme {
//        Box(modifier = Modifier.fillMaxSize()) {
//            Column(modifier = Modifier.align(Alignment.TopCenter)) {
//                SearchBar(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                        .heightIn(max = 56.dp), viewModel
//                )
//            }
//
//            FloatingActionButton(onClick = {}, modifier = Modifier.align(Alignment.BottomEnd), contentColor = Color.White) {
//                Icon(Icons.Filled.Add, "")
//            }
//        }
//    }
//}
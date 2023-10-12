package com.suonk.notepad_plus.ui.note.list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcomposetutorial.ui.theme.NotepadPlusTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotepadPlusTheme {
                AppPortrait()
            }
        }
    }
}

//region ================================================================ SEARCH BAR ================================================================

@Composable
fun SearchBar(viewModel: NotesListViewModel) {
    TextField(value = "Search", onValueChange = { searchText ->
        viewModel.setSearchParameters(searchText)
    }, leadingIcon = {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
    }, textStyle = TextStyle(color = Color(0xFF484644)), modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .heightIn(max = 56.dp)
    )
}

//endregion

//region =========================================================== NOTES COLLECTIONS ===========================================================

@Composable
fun ListOfNotes(list: List<NotesListViewState>) {
    Spacer(modifier = Modifier.height(16.dp))

    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(230.dp)
    ) {
        items(items = list) { item ->
            NoteLayout(item)
        }
    }
}

@Composable
fun NoteLayout(note: NotesListViewState) {
    Surface(
        shape = MaterialTheme.shapes.medium, color = Color(0xFFe5e0dd), modifier = Modifier.padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(255.dp)
        ) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = note.content,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = note.date,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

//endregion

@Composable
fun EntireLayout(
    modifier: Modifier = Modifier,
    viewModel: NotesListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Column {
        SearchBar(viewModel)

        val listOfNotes by viewModel.notesListFlow.collectAsState()
        ListOfNotes(listOfNotes)
    }
}

@Composable
fun HorizontalBottomNavigationView() {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        NavigationBarItem(icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") }, selected = true, onClick = { })
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.AccountBox, contentDescription = "Account") },
            selected = true,
            onClick = { })
    }
}

@Composable
fun AppPortrait() {
    Scaffold(bottomBar = { HorizontalBottomNavigationView() }) { padding ->
        EntireLayout(Modifier.padding(padding))
    }
}
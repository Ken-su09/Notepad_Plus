package fr.delcey.designsystemdemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.delcey.designsystemdemo.ui.theme.Notepad_plusTheme

class DemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Notepad_plusTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column {
                        Text(text = "Demo: Sorting Menu Item")
                        repeat(3) { index ->
//                            NotesSortDropdownMenuItem(
//                                NotesSortDropdownMenuItemViewState(
//                                    "TEXT n°$index",
//                                    0,
//                                    hasDivider = index % 2 == 0,
//                                   isSelected =  index % 3 == 0,
//                                    onSelectedChanged = {
//                                        Toast(currentCompositionLocalContext)
//                                    }
//                                )
//                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Notepad_plusTheme {
        Greeting("Android")
    }
}
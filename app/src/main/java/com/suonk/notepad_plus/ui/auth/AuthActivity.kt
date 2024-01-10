package com.suonk.notepad_plus.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcomposetutorial.ui.theme.NotepadPlusTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.ui.note.list.NotesListActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotepadPlusTheme {
                val viewModel: AuthViewModel = viewModel()
                val isReady by viewModel.isReadyFlow.collectAsState()
                installSplashScreen().apply {
                    setKeepOnScreenCondition {
//                        viewModel.onEvent(AuthDataEvent.AnimationIsFinished(isReady))
                        !isReady
                    }
                }

                Log.i("AnimationFinished", "isReady : $isReady")

                if (isReady) {
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        startActivity(Intent(this, NotesListActivity::class.java))
                        finish()
                    } else {
                        LoginPage({
                            startActivity(Intent(this@AuthActivity, NotesListActivity::class.java))
                        })
                    }
                }
            }
        }

//        binding.signIn.setOnClickListener { mailPasswordSignIn() }
//        viewModel.isFieldsCorrectSingleLiveEvent.observe(this) { areFieldsCorrectlyFilled ->
//            if (areFieldsCorrectlyFilled) {
//                Log.i("LoginWithGoogle", "binding.mailText.text.toString() : ${binding.mailText.text.toString()}")
//                Log.i("LoginWithGoogle", "binding.passwordText.text.toString() : ${binding.passwordText.text.toString()}")
//                FirebaseAuth.getInstance().signInWithEmailAndPassword(
//                    binding.mailText.text.toString(), binding.passwordText.text.toString()
//                ).addOnCompleteListener { task ->
//                    Log.i("LoginWithGoogle", "task : $task")
//                    checkIfTaskIsSuccessfulThenLogin(task)
//                }
//            }
//        }
//        viewModel.toastMessageSingleLiveEvent.observe(this) { message -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
//
//        animationPasswordIconClick()
    }

    //region ====================================================== SIGN IN WITH MAIL/PASSWORD ======================================================

    private fun mailPasswordSignIn() {
//        viewModel.checkIfFieldsAreCorrect(binding.mailText.text?.toString(), binding.passwordText.text?.toString())
    }

    //endregion

    private fun loginSuccessfulToastMessage() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            Toast.makeText(
                this, getString(R.string.welcome_user_after_login, FirebaseAuth.getInstance().currentUser?.displayName), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkIfTaskIsSuccessfulThenLogin(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            Log.i("LoginWithGoogle", "task : $task")
            if (FirebaseAuth.getInstance().currentUser != null) {
                Log.i("LoginWithGoogle", "FirebaseAuth.getInstance().currentUser : ${FirebaseAuth.getInstance().currentUser}")
                loginSuccessfulToastMessage()
                startActivity(Intent(this, NotesListActivity::class.java))
                finish()
            }
        }
    }

//    private fun animationPasswordIconClick() {
//        binding.signUpPasswordGoToVisible.setOnClickListener {
//            val frameAnimation = binding.signUpPasswordGoToVisible.drawable as AnimationDrawable
//            frameAnimation.start()
//
//            CoroutineScope(Dispatchers.Main).launch {
//                delay(525)
//                frameAnimation.stop()
//                binding.signUpPasswordGoToVisible.visibility = View.GONE
//                binding.signUpPasswordGoToInvisible.visibility = View.VISIBLE
//                binding.passwordText.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            }
//        }
//
//        binding.signUpPasswordGoToInvisible.setOnClickListener {
//            val frameAnimation = binding.signUpPasswordGoToInvisible.drawable as AnimationDrawable
//            frameAnimation.start()
//            CoroutineScope(Dispatchers.Main).launch {
//                delay(525)
//                frameAnimation.stop()
//                binding.signUpPasswordGoToInvisible.visibility = View.GONE
//                binding.signUpPasswordGoToVisible.visibility = View.VISIBLE
//                binding.passwordText.transformationMethod = PasswordTransformationMethod.getInstance()
//            }
//        }
//    }
}

@Composable
fun LoginPage(onNavigateToNotesListActivity: () -> Unit, viewModel: AuthViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(coroutineScope) {
        viewModel.authUiEvent.collectLatest { uiEvent ->
            when (uiEvent) {
                is AuthUiEvent.LoginSuccessful -> {
                    onNavigateToNotesListActivity()
                }
            }
        }
    }
//    val customBackgroundDrawable = painterResource(id = R.drawable.custom_edit_text_background)
//
//    val density = LocalDensity.current.density
//    val brush = Brush.horizontalGradient(
//        colors = listOf(
//            Color(0xFFAA00FF), // Start color
//            Color(0xFFFF00AA)  // End color
//        ),
//        startX = 0f,
//        endX = customBackgroundDrawable.intrinsicSize.width / density
//    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Notepad Plus", fontSize = 30.sp, modifier = Modifier.padding(bottom = 16.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.ic_app_icon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
        )

        LoginFormVM()
    }
}

@Composable
private fun LoginFormVM(
    viewModel: AuthViewModel = viewModel()
) {
    LoginForm({ email, password ->
        viewModel.onLoginClickedWithMailAndPassword(email, password)
    })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
private fun LoginForm(onLoginClickedWithMailAndPassword: (String, String) -> Unit = { _, _ -> }, viewModel: AuthViewModel = viewModel()) {
    val email by viewModel.emailValueFlow.collectAsState("")
    val emailIconValidation by viewModel.emailIconValidationValueFlow.collectAsState(R.drawable.ic_check_email_cross)
    val emailIconValidationColor by viewModel.emailIconValidationColorValueFlow.collectAsState(Color.Red)

    val password by viewModel.passwordValueFlow.collectAsState("")
    var passwordVisible by remember { mutableStateOf(false) }

    val rememberFields by viewModel.rememberFieldsValueFlow.collectAsState(false)

    val context = LocalContext.current

    val activityResultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.exception == null) {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { idToken ->
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (FirebaseAuth.getInstance().currentUser != null) {
                                viewModel.addUserToFirestore()

//                                loginSuccessfulToastMessage()
                                // Consider using Navigation to navigate to the next screen instead of finish()
                            }
                        }
                    }
                }
            } else {
                task.exception?.printStackTrace()
            }
        }
    }
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(context, R.string.default_web_client_id))
        .requestEmail().build()

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    DisposableEffect(context) {
        onDispose {
            activityResultLauncher.unregister()
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFe7dfec), Color(0xFFe7dfec))),
                    shape = RoundedCornerShape(60.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            // Email
            TextField(
                value = email,
                onValueChange = { viewModel.onEvent(AuthDataEvent.ChangeEmail(it)) },
                label = {
                    Text(text = "Enter your email")
                },
                colors = TextFieldDefaults.textFieldColors(
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ), placeholder = { if (rememberFields) Text("Email") }, keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Email
                ), keyboardActions = KeyboardActions(onNext = { }), modifier = Modifier
//                .background(brush = MaterialTheme.shapes.medium)
                    .padding(16.dp), textStyle = TextStyle(color = Color.Black, fontSize = 18.sp)
            )

            Icon(
                painter = painterResource(id = emailIconValidation),
                contentDescription = "Red Cross",
                tint = emailIconValidationColor,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
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
            // Password
            TextField(
                value = password,
                onValueChange = { viewModel.onEvent(AuthDataEvent.ChangePassword(it)) },
                label = { Text("Enter your password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                placeholder = { if (rememberFields) Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(onDone = { }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp)
            )

            var icon by remember { mutableIntStateOf(R.drawable.ic_invisible_1) }

            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Password animation",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .clickable {
                        passwordVisible = !passwordVisible
                        icon = if (icon == R.drawable.ic_invisible_1) {
                            R.drawable.ic_password_visible
                        } else {
                            R.drawable.ic_invisible_1
                        }
                    }
            )
        }

        Checkbox(
            checked = rememberFields,
            onCheckedChange = {
                viewModel.onEvent(AuthDataEvent.ChangeRememberFields(it))
            }
        )

        // Login
        Button(
            onClick = {
                onLoginClickedWithMailAndPassword(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
            shape = CircleShape
        ) {
            Text("Login with Mail and Password")
        }

        // Google Login
        Button(
            onClick = {
                activityResultLauncher.launch(googleSignInClient.signInIntent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_google_plus_logo),
                    contentDescription = stringResource(com.suonk.notepad_plus.designsystem.R.string.back_arrow)
                )
                Text("Login with Google")
            }
        }
    }

//    Row {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .background(
//                    brush = Brush.horizontalGradient(listOf(Color(0xFFe7dfec), Color(0xFFe7dfec))),
//                    shape = RoundedCornerShape(60.dp),
//                ),
//            contentAlignment = Alignment.CenterStart,
//        ) {
//            // Email
//            TextField(
//                value = email,
//                onValueChange = { viewModel.onEvent(AuthDataEvent.ChangeEmail(it)) },
//                label = { Text("Enter your email") },
//                placeholder = { if (rememberFields) Text("Email") },
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Done,
//                    keyboardType = KeyboardType.Email
//                ),
//                keyboardActions = KeyboardActions(
//                    onNext = { }
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
////                .background(brush = MaterialTheme.shapes.medium)
//                    .padding(16.dp),
//                textStyle = TextStyle(color = Color.Black)
//            )
//        }
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .background(
//                    brush = Brush.horizontalGradient(listOf(Color(0xFFe7dfec), Color(0xFFe7dfec))),
//                    shape = RoundedCornerShape(60.dp),
//                ),
//            contentAlignment = Alignment.CenterStart,
//        ) {
//            // Password
//            TextField(
//                value = password,
//                onValueChange = { viewModel.onEvent(AuthDataEvent.ChangePassword(it)) },
//                label = { Text("Enter your password") },
//                placeholder = { if (rememberFields) Text("Email") },
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Done,
//                    keyboardType = KeyboardType.Password
//                ),
//                keyboardActions = KeyboardActions(
//                    onDone = { }
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                textStyle = TextStyle(color = Color.Black)
//            )
//        }
//    }
}

//@Preview
//@Composable
//private fun PreviewLoginForm(
//    onLoginClickedWithMailAndPassword: (String, String) -> Unit = { _, _ -> },
//    onLoginClickedWithGoogle: () -> Unit = { },
//) {
//    Column {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .background(
//                    brush = Brush.horizontalGradient(listOf(Color(0xFFe7dfec), Color(0xFFe7dfec))),
//                    shape = RoundedCornerShape(60.dp),
//                ),
//            contentAlignment = Alignment.CenterStart,
//        ) {
//            // Email
//            TextField(
//                value = "",
//                onValueChange = { },
//                label = { Text("Enter your email") },
//                placeholder = { },
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Email
//                ),
//                keyboardActions = KeyboardActions(onNext = { }),
//                modifier = Modifier
//                    .fillMaxWidth()
////                .background(brush = MaterialTheme.shapes.medium)
//                    .padding(16.dp),
//                textStyle = TextStyle(color = Color.Black)
//            )
//        }
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .background(
//                    brush = Brush.horizontalGradient(listOf(Color(0xFFe7dfec), Color(0xFFe7dfec))),
//                    shape = RoundedCornerShape(60.dp),
//                ),
//            contentAlignment = Alignment.CenterStart,
//        ) {
//            // Password
//            TextField(
//                value = "",
//                onValueChange = { },
//                label = { Text("Enter your password") },
//                placeholder = {},
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
//                ),
//                keyboardActions = KeyboardActions(onDone = { }),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                textStyle = TextStyle(color = Color.Black)
//            )
//        }
//
//        // Login
//        Button(
//            onClick = { },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp)
//                .padding(horizontal = 16.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
//            shape = CircleShape
//        ) {
//            Text("Login with Mail and Password")
//        }
//
//        // Google Login
//        Button(
//            onClick = {
//                onLoginClickedWithGoogle()
//            },
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
//            shape = CircleShape,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Row {
//                Icon(
//                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_google_plus_logo),
//                    contentDescription = stringResource(com.suonk.notepad_plus.designsystem.R.string.back_arrow)
//                )
//                Text("Login with Google")
//            }
//        }
//    }
//}
package com.suonk.notepad_plus.ui.auth

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.databinding.ActivityAuthBinding
import com.suonk.notepad_plus.ui.main.MainActivity
import com.suonk.notepad_plus.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private val binding by viewBinding { ActivityAuthBinding.inflate(it) }
    private val viewModel by viewModels<AuthViewModel>()
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (FirebaseAuth.getInstance().currentUser != null) {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//            return
//        }

        setContent {
            LoginPage()
        }

        binding.signIn.setOnClickListener { mailPasswordSignIn() }
        viewModel.isFieldsCorrectSingleLiveEvent.observe(this) { areFieldsCorrectlyFilled ->
            if (areFieldsCorrectlyFilled) {
                Log.i("LoginWithGoogle", "binding.mailText.text.toString() : ${binding.mailText.text.toString()}")
                Log.i("LoginWithGoogle", "binding.passwordText.text.toString() : ${binding.passwordText.text.toString()}")
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.mailText.text.toString(), binding.passwordText.text.toString()
                ).addOnCompleteListener { task ->
                    Log.i("LoginWithGoogle", "task : $task")
                    checkIfTaskIsSuccessfulThenLogin(task)
                }
            }
        }

        binding.googleButton.setOnClickListener { googleSignIn() }
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.exception == null) {
                    try {
                        val account = task.getResult(ApiException::class.java)
                        Log.i("LoginWithGoogle", "account.idToken : ${account.idToken}")
                        account.idToken?.let { authWithGoogle(it) }
                    } catch (e: ApiException) {
                        Log.w("Ken", "Google sign in failed", e)
                    }
                } else {
                    task.exception?.printStackTrace()
                }
            }
        }

        viewModel.toastMessageSingleLiveEvent.observe(this) { message -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }

        animationPasswordIconClick()
    }

    //region ====================================================== SIGN IN WITH MAIL/PASSWORD ======================================================

    private fun mailPasswordSignIn() {
        Log.i("LoginWithGoogle", "Passe par là")
        viewModel.checkIfFieldsAreCorrect(binding.mailText.text?.toString(), binding.passwordText.text?.toString())
    }

    //endregion

    //region ========================================================== SIGN IN WITH GOOGLE =========================================================

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInIntent = googleSignInClient.signInIntent
        Log.i("LoginWithGoogle", "activityResultLauncher : $activityResultLauncher")
        activityResultLauncher?.launch(signInIntent)
    }

    private fun authWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("LoginWithGoogle", "task : $task")
                if (FirebaseAuth.getInstance().currentUser != null) {
                    Log.i("LoginWithGoogle", "FirebaseAuth.getInstance().currentUser : ${FirebaseAuth.getInstance().currentUser}")
                    viewModel.addUserToFirestore()
                    loginSuccessfulToastMessage()
//                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
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
                viewModel.addUserToFirestore()
                loginSuccessfulToastMessage()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun animationPasswordIconClick() {
        binding.signUpPasswordGoToVisible.setOnClickListener {
            val frameAnimation = binding.signUpPasswordGoToVisible.drawable as AnimationDrawable
            frameAnimation.start()

            CoroutineScope(Dispatchers.Main).launch {
                delay(525)
                frameAnimation.stop()
                binding.signUpPasswordGoToVisible.visibility = View.GONE
                binding.signUpPasswordGoToInvisible.visibility = View.VISIBLE
                binding.passwordText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }

        binding.signUpPasswordGoToInvisible.setOnClickListener {
            val frameAnimation = binding.signUpPasswordGoToInvisible.drawable as AnimationDrawable
            frameAnimation.start()
            CoroutineScope(Dispatchers.Main).launch {
                delay(525)
                frameAnimation.stop()
                binding.signUpPasswordGoToInvisible.visibility = View.GONE
                binding.signUpPasswordGoToVisible.visibility = View.VISIBLE
                binding.passwordText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }
}

@Composable
fun LoginPage() {

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
            text = "Notepad Plus",
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 16.dp)
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
    LoginForm { email, password ->
        viewModel.onLoginClicked(email, password)
    }
}

@Preview
@Composable
private fun LoginForm(onLoginClicked: (String, String) -> Unit = { _, _ -> }) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Email
    BasicTextField(
        value = "Test",
        onValueChange = { email = it },
        keyboardOptions = KeyboardOptions.Default,
        keyboardActions = KeyboardActions(
            onNext = { }
        ),
        modifier = Modifier
            .fillMaxWidth()
//                .color
//                .background(brush = brushshape = MaterialTheme.shapes.medium)
            .padding(16.dp),
        textStyle = TextStyle(color = Color.Black)
    )

    // Password
    BasicTextField(
        value = password,
        onValueChange = { password = it },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textStyle = TextStyle(color = Color.Black)
    )

    // Login
    Button(
        onClick = {
            onLoginClicked(email, password)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Login")
    }
}
package com.imaginato.homeworkmvvm.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.imaginato.homeworkmvvm.R
import com.imaginato.homeworkmvvm.exts.getActivity
import com.imaginato.homeworkmvvm.ui.home.MainActivity
import com.imaginato.homeworkmvvm.ui.theme.JetDevsTheme
import org.koin.androidx.compose.koinViewModel

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetDevsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {

    val uiState by viewModel.loginUIState.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = uiState.uiMessage, block = {
        if (uiState.uiMessage != null) {
            val result = snackBarHostState.showSnackbar(
                uiState.uiMessage!!
            )
            if (result == SnackbarResult.Dismissed) {
                viewModel.onDismissSnackBar()
            }
        }
    })

    LaunchedEffect(key1 = uiState.moveToHomeWithId, block = {
        if (uiState.moveToHomeWithId.isNotEmpty()) {
            context.startActivity(Intent(context, MainActivity::class.java).apply {
                putExtra(MainActivity.USER_ID, uiState.moveToHomeWithId)
            })
            context.getActivity()?.finish()
        }
    })

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_logo_jetdevs),
                contentDescription = "logo"
            )

            Spacer(modifier = Modifier.size(30.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.username,
                onValueChange = viewModel::onChangeUsername,
                label = {
                    Text(text = stringResource(id = R.string.username))
                },
                singleLine = true,
                isError = uiState.usernameError != -1,
                supportingText = {
                    if (uiState.usernameError != -1) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = uiState.usernameError),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
            )

            Spacer(modifier = Modifier.size(4.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.password,
                onValueChange = viewModel::onChangePassword,
                label = {
                    Text(text = stringResource(id = R.string.password))
                },
                visualTransformation = if(isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                isError = uiState.passwordError != -1,
                supportingText = {
                    if (uiState.passwordError != -1) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = uiState.passwordError),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if(isPasswordVisible) {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(imageVector = Icons.Filled.VisibilityOff, contentDescription = "on")
                        }
                    }else{
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(imageVector = Icons.Filled.Visibility, contentDescription = "on")
                        }
                    }
                },
            )

            Spacer(modifier = Modifier.size(30.dp))

            Button(
                enabled = !uiState.isLoading,
                onClick = viewModel::loginUser,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.animation.AnimatedVisibility(visible = uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(25.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    androidx.compose.animation.AnimatedVisibility(!uiState.isLoading) {
                        Text(text = stringResource(id = R.string.login))
                    }
                }
            }
        }
    }
}

package com.imaginato.homeworkmvvm.ui.login

import androidx.lifecycle.viewModelScope
import com.imaginato.homeworkmvvm.R
import com.imaginato.homeworkmvvm.domain.AuthRepository
import com.imaginato.homeworkmvvm.domain.Resource
import com.imaginato.homeworkmvvm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : BaseViewModel() {

    private val _loginStateFlow = MutableStateFlow(LoginUIState())
    val loginUIState = _loginStateFlow.asStateFlow()

    fun onChangeUsername(name: String) = _loginStateFlow.update {
        it.copy(
            username = name,
            usernameError = if(name.isNotEmpty() && it.usernameError != -1) -1 else it.usernameError
        )
    }

    fun onChangePassword(password: String) = _loginStateFlow.update {
        it.copy(
            password = password,
            passwordError = if(password.isNotEmpty() && it.passwordError != -1) -1 else it.passwordError
        )
    }

    fun loginUser() {
        viewModelScope.launch {
            val result = checkValidation()
            if (!result) {
                return@launch
            }
            _loginStateFlow.update { it.copy(isLoading = true) }
            when (val data = repository.loginUser(
                username = loginUIState.value.username,
                password = loginUIState.value.password
            )) {
                is Resource.SuccessWithHeader -> {
                    _loginStateFlow.update { it.copy(moveToHomeWithId = data.data?.data?.userId ?: "", isLoading = false) }
                }

                else -> {
                    _loginStateFlow.update {
                        it.copy(uiMessage = (data as Resource.Error).message, isLoading = false)
                    }
                }
            }
        }
    }

    private fun checkValidation() = with(_loginStateFlow.value) {
        if (username.isEmpty()) {
            _loginStateFlow.update { it.copy(usernameError = R.string.please_enter_username) }
            return@with false
        } else if (password.isEmpty()) {
            _loginStateFlow.update { it.copy(passwordError = R.string.please_enter_password) }
            return@with false
        } else {
            return@with true
        }
    }

    fun onDismissSnackBar() {
        _loginStateFlow.update { it.copy(uiMessage = null) }
    }
}

data class LoginUIState(
    val isLoading: Boolean = false,
    val username: String = "",
    val password: String = "",

    val uiMessage: String? = null,

    val usernameError: Int = -1,
    val passwordError: Int = -1,

    val moveToHomeWithId: String = ""
)
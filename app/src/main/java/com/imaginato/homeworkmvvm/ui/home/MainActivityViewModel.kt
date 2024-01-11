package com.imaginato.homeworkmvvm.ui.home

import androidx.lifecycle.viewModelScope
import com.imaginato.homeworkmvvm.domain.AuthRepository
import com.imaginato.homeworkmvvm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: AuthRepository) : BaseViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun getUserInfo(userId: String) {
        viewModelScope.launch {
            val userdata = repository.getUser(userId = userId)
            _uiState.update {
                it.copy(isLoading = false, username = userdata?.userName ?: "", userId = userdata?.userId ?: "")
            }
        }
    }
}

data class MainUiState(
    val isLoading: Boolean = true,
    val username: String = "",
    val userId: String = ""
)
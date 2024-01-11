package com.imaginato.homeworkmvvm

import com.imaginato.homeworkmvvm.data.remote.login.response.Data
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import com.imaginato.homeworkmvvm.domain.AuthRepository
import com.imaginato.homeworkmvvm.domain.Resource
import com.imaginato.homeworkmvvm.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var loginViewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        loginViewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `onChangeUsername update username`() {
        val name = "user"

        loginViewModel.onChangeUsername(name)

        assertEquals(loginViewModel.loginUIState.value.username, name)
    }

    @Test
    fun `onChangePassword update password`() {
        val password = "password"

        loginViewModel.onChangePassword(password)

        assertEquals(loginViewModel.loginUIState.value.password, password)
    }

    @Test
    fun `loginUser with empty username`() = runTest(testDispatcher) {
        val emptyUsername = ""

        loginViewModel.onChangeUsername(emptyUsername)
        loginViewModel.loginUser()

        advanceUntilIdle()

        assertEquals(R.string.please_enter_username, loginViewModel.loginUIState.value.usernameError)
    }

    @Test
    fun `loginUser with empty password`() = runTest(testDispatcher) {
        val emptyUsername = "user"
        val emptyPassword = ""

        loginViewModel.onChangeUsername(emptyUsername)
        loginViewModel.onChangePassword(emptyPassword)
        loginViewModel.loginUser()

        advanceUntilIdle()

        assertEquals(R.string.please_enter_password, loginViewModel.loginUIState.value.passwordError)
    }

    @Test
    fun `loginUser success`() = runTest(testDispatcher) {
        val username = "user"
        val password = "password"
        val userId = "111"
        val successResource = Resource.SuccessWithHeader<LoginResponse?>(LoginResponse(Data(userId = userId)))

        whenever(authRepository.loginUser(username, password)).thenReturn(successResource)

        loginViewModel.onChangeUsername(username)
        loginViewModel.onChangePassword(password)
        loginViewModel.loginUser()

        advanceUntilIdle()

        assertEquals(userId, loginViewModel.loginUIState.value.moveToHomeWithId)
        assertEquals(false, loginViewModel.loginUIState.value.isLoading)
        assertEquals(null, loginViewModel.loginUIState.value.uiMessage)

        verify(authRepository).loginUser(username, password)
    }

    @Test
    fun `loginUser failure`() = runTest(testDispatcher) {
        val username = "user"
        val password = "password"
        val errorMessage = "Login failed"
        val errorResource = Resource.Error<LoginResponse?>(message = errorMessage, data = null)

        whenever(authRepository.loginUser(username, password)).thenReturn(errorResource)

        loginViewModel.onChangeUsername(username)
        loginViewModel.onChangePassword(password)
        loginViewModel.loginUser()

        advanceUntilIdle()

        assertEquals("", loginViewModel.loginUIState.value.moveToHomeWithId)
        assertEquals(false, loginViewModel.loginUIState.value.isLoading)
        assertEquals(errorMessage, loginViewModel.loginUIState.value.uiMessage)

        verify(authRepository).loginUser(username, password)
    }
}
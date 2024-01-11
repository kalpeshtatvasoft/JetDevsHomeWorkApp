package com.imaginato.homeworkmvvm

import com.imaginato.homeworkmvvm.data.local.login.UserEntity
import com.imaginato.homeworkmvvm.domain.AuthRepository
import com.imaginato.homeworkmvvm.ui.home.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainActivityViewModelTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        mainActivityViewModel = MainActivityViewModel(authRepository)
    }

    @Test
    fun `getUserInfo success`() = runTest(testDispatcher) {
        val userId = "111"

        val entity = UserEntity(
            userId = "userId",
            userName = "username",
            isDeleted = false,
            xAcc = "xAccHeader",
        )

        whenever(authRepository.getUser(userId)).thenReturn(entity)

        mainActivityViewModel.getUserInfo(userId)

        advanceUntilIdle()

        assertEquals(false, mainActivityViewModel.uiState.value.isLoading)
        assertEquals("username", mainActivityViewModel.uiState.value.username)
        assertEquals("userId", mainActivityViewModel.uiState.value.userId)
    }

    @Test
    fun `getUserInfo failed`() = runTest(testDispatcher) {
        val userId = "111"

        whenever(authRepository.getUser(userId)).thenReturn(null)

        mainActivityViewModel.getUserInfo(userId)

        advanceUntilIdle()

        assertEquals(false, mainActivityViewModel.uiState.value.isLoading)
        assertEquals("", mainActivityViewModel.uiState.value.username)
        assertEquals("", mainActivityViewModel.uiState.value.userId)
    }
}
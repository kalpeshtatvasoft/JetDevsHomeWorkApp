package com.imaginato.homeworkmvvm

import com.imaginato.homeworkmvvm.data.local.login.UserDao
import com.imaginato.homeworkmvvm.data.local.login.UserEntity
import com.imaginato.homeworkmvvm.data.remote.HeaderInterceptor
import com.imaginato.homeworkmvvm.data.remote.ResourceHelper
import com.imaginato.homeworkmvvm.data.remote.login.ApiService
import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.data.remote.login.response.Data
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import com.imaginato.homeworkmvvm.domain.AuthDataRepository
import com.imaginato.homeworkmvvm.domain.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AuthDataRepositoryTest {

    private lateinit var userDao: UserDao
    private lateinit var apiService: ApiService
    private lateinit var resourceHelper: ResourceHelper
    private lateinit var authDataRepository: AuthDataRepository

    @Before
    fun setup() {
        userDao = mock()
        apiService = mock()
        resourceHelper = mock()

        authDataRepository = AuthDataRepository(userDao, apiService, resourceHelper)
    }

    @Test
    fun `loginUser success`() = runTest {
        val username = "username"
        val password = "password"
        val loginRequest = LoginRequest(username, password)
        val loginResponse = LoginResponse(Data(false, "username", "userId"))
        val xAccHeader = "XAccHeader"

        val entity = UserEntity(
            userId = "userId",
            userName = "username",
            isDeleted = false,
            xAcc = xAccHeader,
        )

        whenever(apiService.loginUser(loginRequest)).thenReturn(
            Response.success(
                loginResponse,
                Headers.Builder().add(HeaderInterceptor.X_ACC, xAccHeader).build()
            )
        )

        val result = authDataRepository.loginUser(username, password)

        advanceUntilIdle()

        verify(apiService).loginUser(loginRequest)
        verify(userDao).getUser("userId")
        verify(userDao).insertUser(entity)
        assert(result is Resource.SuccessWithHeader)
    }

    @Test
    fun `loginUser failure`() = runTest {
        val username = "user"
        val password = "password"
        val loginRequest = LoginRequest(username, password)

        whenever(apiService.loginUser(loginRequest)).thenReturn(
            Response.error(
                401,
                """{ "errorCode": "01",
                    "errorMessage": "your password is incorrect."
                    }""".toResponseBody()
            )
        )

        val result = authDataRepository.loginUser(username, password)

        verify(apiService).loginUser(loginRequest)
        assert(result is Resource.Error)
        assertEquals((result as Resource.Error).message, "your password is incorrect.")
    }

}
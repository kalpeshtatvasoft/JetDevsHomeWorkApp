package com.imaginato.homeworkmvvm.domain

import com.imaginato.homeworkmvvm.data.local.login.UserDao
import com.imaginato.homeworkmvvm.data.local.login.UserEntity
import com.imaginato.homeworkmvvm.data.remote.HeaderInterceptor
import com.imaginato.homeworkmvvm.data.remote.ResourceHelper
import com.imaginato.homeworkmvvm.data.remote.login.ApiService
import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.data.remote.login.response.Data
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthDataRepository(
    private val dao: UserDao,
    private var api: ApiService,
    helper: ResourceHelper
) : BaseRepository(helper), AuthRepository {

    override suspend fun loginUser(username: String, password: String): Resource<LoginResponse?> =
        withContext(Dispatchers.IO) {
            val result = safeApiCall(true) { api.loginUser(LoginRequest(username, password)) }

            if (result is Resource.SuccessWithHeader) {
                saveUser(result.data?.data, result.header?.get(HeaderInterceptor.X_ACC))
            }

            result
        }

    override suspend fun getUser(userId: String): UserEntity? = withContext(Dispatchers.IO) {
        dao.getUser(userId)
    }

    private suspend fun saveUser(data: Data?, xacc: String?) {
        val user = dao.getUser(data?.userId ?: "")
        if(user == null) {
            dao.insertUser(
                UserEntity(
                    userId = data?.userId ?: "",
                    userName = data?.userName ?: "",
                    isDeleted = data?.isDeleted ?: false,
                    xAcc = xacc ?: "",
                )
            )
        }else{
            dao.updateUser(
                user.copy(xAcc = xacc ?: "")
            )
        }
    }
}
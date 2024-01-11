package com.imaginato.homeworkmvvm.domain

import com.imaginato.homeworkmvvm.data.local.login.UserEntity
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse

interface AuthRepository {
    suspend fun loginUser(username: String, password: String): Resource<LoginResponse?>

    suspend fun getUser(userId: String): UserEntity?
}
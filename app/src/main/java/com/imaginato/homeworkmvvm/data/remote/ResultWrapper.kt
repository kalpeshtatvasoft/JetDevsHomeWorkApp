package com.imaginato.homeworkmvvm.data.remote

import com.imaginato.homeworkmvvm.data.remote.login.response.BaseResponse

sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T): ResultWrapper<T>()
    data class GenericError(val code: Int? = null, val error: BaseResponse? = null): ResultWrapper<Nothing>()
    data class NetworkError(val messageRes: Int?): ResultWrapper<Nothing>()
}
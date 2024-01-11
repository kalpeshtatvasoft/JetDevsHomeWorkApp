package com.imaginato.homeworkmvvm.domain

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val header: Map<String, String>? = null,
) {
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String? = null, data: T? = null): Resource<T>(data, message)

    class SuccessWithHeader<T>(data: T, header: Map<String,String>? = null): Resource<T>(data, header = header)
}
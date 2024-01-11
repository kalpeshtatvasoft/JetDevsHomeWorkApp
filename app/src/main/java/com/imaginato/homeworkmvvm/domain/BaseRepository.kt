package com.imaginato.homeworkmvvm.domain

import com.google.gson.Gson
import com.imaginato.homeworkmvvm.data.remote.ResourceHelper
import com.imaginato.homeworkmvvm.data.remote.login.response.BaseResponse
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseRepository(private val helper: ResourceHelper) {

    suspend fun <T> safeApiCall(
        fetchHeader: Boolean = true,
        apiCall: suspend () -> Response<T?>,
    ): Resource<T?> {
        return try {
            parserResponse(apiCall.invoke(), fetchHeader)
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val errorResponse = convertErrorBody(throwable)
                    Resource.Error(message = errorResponse?.errorMessage)
                }

                is IOException -> Resource.Error(message = helper.noInternetConnection)

                else -> {
                    Resource.Error(null, null)
                }
            }
        }
    }

    private fun <T> parserResponse(response: Response<T>, fetchHeader: Boolean): Resource<T?> {
        if(response.isSuccessful) {
            return if (fetchHeader)
                Resource.SuccessWithHeader(
                    data = response.body(),
                    header = response.headers().toMap()
                )
            else
                Resource.Success(response.body())
        }else {
            throw HttpException(response)
        }
    }

    private fun convertErrorBody(throwable: HttpException): BaseResponse? {
        return try {
            throwable.response()?.errorBody()?.string()?.let {
                Gson().fromJson(it, BaseResponse::class.java)
            }
        } catch (exception: Exception) {
            return BaseResponse(errorMessage = throwable.message)
        }
    }

}
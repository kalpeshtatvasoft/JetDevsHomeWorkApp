package com.imaginato.homeworkmvvm.data.remote.login.response

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName("errorMessage")
    val errorMessage: String? = null,

    @SerializedName("errorCode")
    val errorCode: String? = null
)
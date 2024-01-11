package com.imaginato.homeworkmvvm.data.remote.login.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val data: Data? = null,
): BaseResponse()

data class Data(
    @SerializedName("isDeleted")
    val isDeleted: Boolean? = null,

    @SerializedName("userName")
    val userName: String? = null,

    @SerializedName("userId")
    val userId: String? = null
)

package com.imaginato.homeworkmvvm.data.remote

import android.util.Log
import com.imaginato.homeworkmvvm.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        request = request.newBuilder()
            .addHeader(IMSI, BuildConfig.IMSI)
            .addHeader(IMEI, BuildConfig.IMEI)
            .build()
        return chain.proceed(request)
    }

    companion object {
        const val X_ACC = "X-Acc"
        const val IMSI = "IMSI"
        const val IMEI = "IMEI"
    }
}
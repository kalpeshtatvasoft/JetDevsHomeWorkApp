package com.imaginato.homeworkmvvm.data.remote

import android.app.Application
import com.imaginato.homeworkmvvm.R

class ResourceHelper(
    private val application: Application
) {
    val noInternetConnection: String
        get() = application.getString(R.string.no_internet_connection)
}
package com.imaginato.homeworkmvvm.di

import android.app.Application
import androidx.room.Room
import com.imaginato.homeworkmvvm.BuildConfig
import com.imaginato.homeworkmvvm.data.local.AppDatabase
import com.imaginato.homeworkmvvm.data.local.login.UserDao
import com.imaginato.homeworkmvvm.data.remote.HeaderInterceptor
import com.imaginato.homeworkmvvm.data.remote.ResourceHelper
import com.imaginato.homeworkmvvm.data.remote.login.ApiService
import com.imaginato.homeworkmvvm.domain.AuthDataRepository
import com.imaginato.homeworkmvvm.domain.AuthRepository
import com.imaginato.homeworkmvvm.ui.home.MainActivityViewModel
import com.imaginato.homeworkmvvm.ui.login.LoginViewModel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val commonModule = module {
    single { provideResourceHelper(androidApplication()) }
}

val databaseModule = module {
    single { provideDatabase(androidApplication()) }
    single { provideUserDao(get()) }
}

val netModules = module {
    single { provideInterceptors() }
    single { provideOkHttpClient(get()) }
    single { provideRetrofit(get()) }
}

val apiModules = module {
    single { provideApiService(get()) }
}

val repositoryModules = module {
    single { provideAuthRepo(get(), get(), get()) }
}

val viewModelModules = module {
    viewModel {
        MainActivityViewModel(get())
    }
    viewModel {
        LoginViewModel(get())
    }
}

private fun provideAuthRepo(dao: UserDao, api: ApiService, helper: ResourceHelper): AuthRepository {
    return AuthDataRepository(dao, api, helper)
}

private fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

private fun provideDatabase(application: Application): AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, "I-Database")
        .fallbackToDestructiveMigration()
        .build()
}

private fun provideUserDao(database: AppDatabase): UserDao {
    return database.userDao
}

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

private fun provideOkHttpClient(interceptors: ArrayList<Interceptor>): OkHttpClient {
    val clientBuilder = OkHttpClient.Builder()
    clientBuilder.readTimeout(2, TimeUnit.MINUTES)
    clientBuilder.connectTimeout(2, TimeUnit.MINUTES)
    clientBuilder.addInterceptor(HeaderInterceptor())
    interceptors.forEach { clientBuilder.addInterceptor(it) }
    return clientBuilder.build()
}

private fun provideInterceptors(): ArrayList<Interceptor> {
    val interceptors = arrayListOf<Interceptor>()
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    interceptors.add(loggingInterceptor)
    return interceptors
}

private fun provideResourceHelper(application: Application): ResourceHelper {
    return ResourceHelper(application)
}

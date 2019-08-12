package org.damienoreilly.threeutils.di

import androidx.work.WorkManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.damienoreilly.threeutils.BuildConfig
import org.damienoreilly.threeutils.di.RetroFitClient.createApiService
import org.damienoreilly.threeutils.di.RetroFitClient.provideOkHttpClient
import org.damienoreilly.threeutils.repository.*
import org.damienoreilly.threeutils.viewmodel.My3SetupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.threeten.bp.Duration
import org.threeten.bp.temporal.ChronoUnit
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { provideOkHttpClient() }
    single { createApiService<My3API>(get(), getProperty("MY3_URL")) }
    single { createApiService<ThreePlusAPI>(get(), getProperty("3PLUS_URL")) }
}

val appModule = module {
    single<PreferenceStorage> { SharedPreferenceStorage(get()) }
    single<My3Repository> { My3RepositoryImpl(get()) }
    single<ThreePlusRepository> { ThreePlusRepositoryImpl(get()) }
    single { WorkManager.getInstance(get()) }
    viewModel { My3SetupViewModel(get(), get(), get()) }
}

object RetroFitClient {
    private val loggingLevel = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    private val logging = HttpLoggingInterceptor()

    private val duration = Duration.of(30, ChronoUnit.SECONDS)

    fun provideOkHttpClient(): OkHttpClient {
        logging.level = loggingLevel
        return OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(duration.seconds, TimeUnit.SECONDS)
                .readTimeout(duration.seconds, TimeUnit.SECONDS)
                .writeTimeout(duration.seconds, TimeUnit.SECONDS)
                .build()
    }

    inline fun <reified T> createApiService(client: OkHttpClient, baseUrl: String): T {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(baseUrl)
                .client(client)
                .build()

        return retrofit.create(T::class.java)
    }

}
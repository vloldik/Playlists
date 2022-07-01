package ru.vladik.playlists.api.lastfm

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.vladik.playlists.constants.Strings

class LastFmApi(private val token: String) {


    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor {
        val url = it.request().url().newBuilder().addQueryParameter("api_key", token).build()
        val request = it.request().newBuilder().url(url).build()
        return@addInterceptor it.proceed(request)
    }.build()

    fun getService(): LastFmService {
        val client = Retrofit.Builder()
            .baseUrl(Strings.LAST_FM_API_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return client.create(LastFmService::class.java)
    }
}
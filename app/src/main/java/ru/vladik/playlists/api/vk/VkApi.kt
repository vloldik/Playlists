package ru.vladik.playlists.api.vk

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.vladik.playlists.constants.VK_API_BASE_URL

class VkApi(private val token: String) {
    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor {
        val url = it.request().url().newBuilder().addQueryParameter("access_token", token).build()
        val request = it.request().newBuilder().url(url).build()
        return@addInterceptor it.proceed(request)
    }.build()

    private val gsonBuilder = GsonBuilder()

    fun getService(): VkService {
        val gson = gsonBuilder


        val client = Retrofit.Builder()
            .baseUrl(VK_API_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson.create()))
            .client(client)
            .build()

        return client.create(VkService::class.java)
    }

}
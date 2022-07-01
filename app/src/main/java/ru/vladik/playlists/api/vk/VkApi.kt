package ru.vladik.playlists.api.vk

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.vladik.playlists.constants.Strings
import ru.vladik.playlists.utils.SuperclassExclusionStrategy

class VkApi(private val token: String) {
    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor {
        val url = it.request().url().newBuilder().addQueryParameter("access_token", token).build()
        val request = it.request().newBuilder().url(url).build()
        return@addInterceptor it.proceed(request)
    }.build()

    private val gsonBuilder = GsonBuilder()

    fun getService(): VkService {
        val gson = gsonBuilder
            .addDeserializationExclusionStrategy(SuperclassExclusionStrategy)
            .addSerializationExclusionStrategy(SuperclassExclusionStrategy)

        val client = Retrofit.Builder()
            .baseUrl(Strings.VK_API_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson.create()))
            .client(client)
            .build()

        return client.create(VkService::class.java)
    }
}
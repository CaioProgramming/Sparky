package com.silent.core.instagram

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val API_URL = "https://www.instagram.com/"


class InstagramService {

    private val retroFitService: Retrofit by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(interceptor).build()
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(API_URL).build()
    }

    suspend fun getUserInfo(userName: String): InstagramResponse {
        val instagramApi = retroFitService.create(InstagramApi::class.java)
        return instagramApi.getUserInfo(userName)
    }

    private fun mapToHTMLToResponse(document: Document): InstagramWebResponse {

        val script = document.getElementsByTag("script")
        val requiredScript = script.find { it.data().contains("window._sharedData") }
        //val response = Gson().fromJson(requiredScript!!.data(), InstagramWebResponse::class.java)
        //return response
        val (key, value) = document
            .select("script")
            .map(Element::data)
            .first { "window._sharedData" in it }
            .split("=")
            .map(String::trim)


        val pureValue = value.replace(Regex("""["';]"""), "")
        print("response ->\n $value")
        val map = HashMap<String, String>()

        return Gson().fromJson(pureValue.trim(), InstagramWebResponse::class.java)

    }
}
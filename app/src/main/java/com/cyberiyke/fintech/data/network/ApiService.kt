package com.cyberiyke.newsApp.data.network

import com.cyberiyke.newsApp.data.model.NewsResponse
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface ApiService {

        @GET("/v2/top-headlines")
        suspend fun getTopHeadline(
            @Query("country") country:String? = null,
            @Query("category") category:String? = null,
            @Query("language") language:String = "en",
            @Query("sources") sources: String? = null,
            @Query("q") keyword: String? = null,
            @Query("pageSize") pageSize: Int = 20, // Added default value
            @Query("page") page: Int = 1 // Added default value

        ): retrofit2.Response<NewsResponse>


        // for searching
        @GET("/v2/everything")
        suspend fun getEveryThing(
            @Query("q") query: String,
            @Query("sources") sources: String? = null,
            @Query("from") from:String? = null,
            @Query("language") language: String = "en",
            @Query("sortBy") sortBy:String = "publishedAt",
            @Query("pageSize") pageSize: Int = 20, // Added default value
            @Query("page") page: Int = 1 // Added default value
        ): retrofit2.Response<NewsResponse>

}
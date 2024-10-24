package com.cyberiyke.newsApp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.cyberiyke.newsApp.data.local.ArticleDao
import com.cyberiyke.newsApp.data.local.ArticleEntity
import com.cyberiyke.newsApp.data.network.ApiService
import timber.log.Timber
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val apiService: ApiService,
    private val articleDao: ArticleDao
) {

     // here we are fetching articles from the api and caching them in room database
    suspend fun getTopHeadlines(
        country: String?,
        category: String?,
        language: String,
        pageSize: Int,
        page: Int
    ){

        // firstly fetch from API
        val response =  apiService.getTopHeadline(
            country,
            category,
            language,
            null,
            null,
            pageSize,
            page
        )

         // next we cache the fetched articles response into our database
         try {
             if (response.isSuccessful){
                 response.body()?.let { newsResponse ->
                     val articleEntities = newsResponse.articles.map { article ->
                         ArticleEntity(
                             articleTitle = article.title?:"",
                             articleDescription = article.description?:"",
                             articleUrl = article.url?:"",
                             publisedAt = article.publishedAt?:"",
                             articleDateTime = article.publishedAt?:"",
                             articleUrlToImage = article.urlToImage?:"",
                             articleSource = article.source.name,
                             isFavorite = false
                         )
                     }
                     Timber.d("Inserting ${articleEntities.size} articles into the database")
                     articleDao.clearNonFavoriteData()
                     articleDao.insertArticle(articleEntities)
                 }
             }

         }catch (e: Exception){
             Timber.e(e, "Error occurred while fetching articles")
         }
    }




    suspend fun searchArticles(
        query: String,
        sources: String? = null,
        from: String? = null,
        language: String = "en",
        sortBy: String = "publishedAt",
        pageSize: Int = 20,
        page: Int = 1
    ): List<ArticleEntity> {
        // Fetch from the API
        val response = apiService.getEveryThing(query, sources, from, language, sortBy, pageSize, page)

        return try {
            if (response.isSuccessful) {
                response.body()?.articles?.map { article ->
                    ArticleEntity(
                        articleTitle = article.title ?: "",
                        articleDescription = article.description ?: "",
                        articleUrl = article.url ?: "",
                        publisedAt = article.publishedAt?:"",
                        articleDateTime = article.publishedAt ?: "",
                        articleUrlToImage = article.urlToImage ?: "",
                        articleSource = article.source.name,
                        isFavorite = false
                    )
                } ?: emptyList()
            } else {
                Timber.e("Error: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error occurred while searching articles")
            emptyList()
        }
    }



    // Fetch cached articles when offline
    fun getCachedArticles(): LiveData<List<ArticleEntity>> {
        Timber.tag("meme").d("getCachedArticles: ${articleDao.getAllArticles().value}")
        return articleDao.getAllArticles()
    }

    suspend fun updateFavoriteStatus(articleId: Int, isFavourite:Boolean){
        articleDao.updateFavoriteStatus(articleId, isFavourite)
    }

    fun getFavouriteArticle():LiveData<List<ArticleEntity>>{
        return articleDao.getFavoriteArticles()
    }

    suspend fun insertSingle(articleEntity: ArticleEntity){
        articleDao.insertArticle(listOf(articleEntity))
    }

}
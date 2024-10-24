package com.cyberiyke.newsApp.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.cyberiyke.newsApp.data.local.AppDatabase
import com.cyberiyke.newsApp.data.local.MIGRATION_1_2
import com.cyberiyke.newsApp.data.network.ApiService
import com.google.firebase.crashlytics.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext  context: Context): AppDatabase{
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME )
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(database: AppDatabase) = database.getArticleDao()



    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        return  FirebaseCrashlytics.getInstance()
    }

    @Provides
    @Singleton
    fun provideTimberTree(): Timber.Tree{
        return if(BuildConfig.DEBUG){
            Timber.DebugTree()

        }else{
            CrashReportingTree()
        }
    }

    // Custom Timber tree for release builds to log to Crashlytics
    class CrashReportingTree: Timber.Tree(){
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

            if (priority == Log.VERBOSE || priority == Log.DEBUG) return

            // Log to Crashlytics for warning, error, and assert priorities
            FirebaseCrashlytics.getInstance().log(message)

            if (t != null){
                Timber.d(t)
                FirebaseCrashlytics.getInstance().recordException(t)
            }
        }
    }

}
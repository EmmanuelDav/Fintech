package com.cyberiyke.newsApp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // migration for adding a new column
        database.execSQL("ALTER TABLE articles ADD COLUMN new_column_name TEXT DEFAULT ''")
    }
}


@Database( entities = [ArticleEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object {
        const val DATABASE_NAME = "news_app_db" // Centralized database name
    }
}
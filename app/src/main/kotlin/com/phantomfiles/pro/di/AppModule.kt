package com.phantomfiles.pro.di

import android.content.Context
import androidx.room.Room
import com.phantomfiles.pro.data.local.BookmarkDao
import com.phantomfiles.pro.data.local.FilesCacheDao
import com.phantomfiles.pro.data.local.OperationLogDao
import com.phantomfiles.pro.data.local.PhantomDatabase
import com.phantomfiles.pro.data.local.RecycleBinDao
import com.phantomfiles.pro.data.local.ScanResultDao
import com.phantomfiles.pro.data.local.VaultDao
import com.phantomfiles.pro.data.remote.GeminiApi
import com.phantomfiles.pro.data.remote.GroqApi
import javax.inject.Named
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PhantomDatabase =
        Room.databaseBuilder(context, PhantomDatabase::class.java, "phantom_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideFilesCacheDao(db: PhantomDatabase): FilesCacheDao = db.filesCacheDao()
    @Provides fun provideRecycleBinDao(db: PhantomDatabase): RecycleBinDao = db.recycleBinDao()
    @Provides fun provideVaultDao(db: PhantomDatabase): VaultDao = db.vaultDao()
    @Provides fun provideScanResultDao(db: PhantomDatabase): ScanResultDao = db.scanResultDao()
    @Provides fun provideBookmarkDao(db: PhantomDatabase): BookmarkDao = db.bookmarkDao()
    @Provides fun provideOperationLogDao(db: PhantomDatabase): OperationLogDao = db.operationLogDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

    @Provides
    @Singleton
    fun provideGroqApi(client: OkHttpClient): GroqApi =
        Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApi::class.java)

    @Provides
    @Singleton
    fun provideGeminiApi(client: OkHttpClient): GeminiApi =
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)
}

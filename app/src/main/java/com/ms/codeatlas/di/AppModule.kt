package com.ms.codeatlas.di

import android.app.Application
import androidx.room.Room
import com.ms.codeatlas.core.data.local.database.RepoDatabase
import com.ms.codeatlas.core.data.remote.api.GithubApiService
import com.ms.codeatlas.core.data.repository.RepoRepositoryImpl
import com.ms.codeatlas.domain.repository.RepoRepository
import com.ms.codeatlas.domain.usecase.GetReposUseCase
import com.ms.codeatlas.domain.usecase.SearchReposUseCase
import com.ms.codeatlas.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides application-wide dependencies.
 * Installed in [SingletonComponent], so all provided dependencies are singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides a singleton [OkHttpClient] with a basic HTTP logging interceptor.
     *
     * @return Configured [OkHttpClient].
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Provides a singleton [Retrofit] instance configured with Moshi converter and OkHttpClient.
     *
     * @param okHttpClient The HTTP client used by Retrofit.
     * @return Configured [Retrofit] instance.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    /**
     * Provides a singleton [GithubApiService] implementation.
     *
     * @param retrofit The [Retrofit] instance.
     * @return Implementation of [GithubApiService].
     */
    @Provides
    @Singleton
    fun provideGithubApiService(retrofit: Retrofit): GithubApiService {
        return retrofit.create(GithubApiService::class.java)
    }

    /**
     * Provides a singleton [RepoDatabase] instance using Room.
     *
     * @param app The application context.
     * @return Configured [RepoDatabase].
     */
    @Provides
    @Singleton
    fun provideRepoDatabase(app: Application): RepoDatabase {
        return Room.databaseBuilder(
            app,
            RepoDatabase::class.java,
            "repo_db"
        ).build()
    }

    /**
     * Provides a singleton implementation of [RepoRepository].
     *
     * @param apiService The GitHub API service.
     * @param database The local Room database.
     * @return Implementation of [RepoRepository].
     */
    @Provides
    @Singleton
    fun provideRepoRepository(
        apiService: GithubApiService,
        database: RepoDatabase
    ): RepoRepository {
        return RepoRepositoryImpl(apiService, database.repoDao())
    }

    /**
     * Provides a singleton [GetReposUseCase].
     *
     * @param repository The repository to be used by the use case.
     * @return Configured [GetReposUseCase].
     */
    @Provides
    @Singleton
    fun provideGetReposUseCase(repository: RepoRepository): GetReposUseCase {
        return GetReposUseCase(repository)
    }

    /**
     * Provides a singleton [SearchReposUseCase].
     *
     * @param repository The repository to be used by the use case.
     * @return Configured [SearchReposUseCase].
     */
    @Provides
    @Singleton
    fun provideSearchReposUseCase(repository: RepoRepository): SearchReposUseCase {
        return SearchReposUseCase(repository)
    }
}
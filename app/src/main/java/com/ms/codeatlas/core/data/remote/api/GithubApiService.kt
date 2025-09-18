package com.ms.codeatlas.core.data.remote.api

import com.ms.codeatlas.core.data.remote.model.RemoteRepo
import com.ms.codeatlas.core.data.remote.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for accessing GitHub API endpoints.
 */
interface GithubApiService {

    /**
     * Fetches a list of repositories belonging to a specific organization.
     *
     * @param org The organization login name.
     * @param page The page number for pagination.
     * @param perPage Number of repositories per page (default is 30).
     * @return A [Response] wrapping a [List] of [RemoteRepo].
     */
    @GET("orgs/{org}/repos")
    suspend fun getOrganizationRepos(
        @Path("org") org: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30
    ): Response<List<RemoteRepo>>

    /**
     * Searches repositories on GitHub matching the given query.
     *
     * @param query The search query (e.g., "language:kotlin stars:>100").
     * @param page The page number for pagination.
     * @param perPage Number of repositories per page (default is 30).
     * @return A [Response] wrapping a [SearchResponse] object containing matching repositories.
     */
    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30
    ): Response<SearchResponse>

}
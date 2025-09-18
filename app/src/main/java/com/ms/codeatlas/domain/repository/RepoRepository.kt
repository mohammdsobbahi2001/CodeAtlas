package com.ms.codeatlas.domain.repository

import com.ms.codeatlas.domain.model.Repo
import com.ms.codeatlas.util.NetworkResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for fetching GitHub repositories and their owners.
 */
interface RepoRepository {

    /**
     * Fetches a paginated list of repositories for a given organization.
     * The result may be retrieved from cache or network.
     *
     * @param org The GitHub organization name.
     * @param page The page number for pagination.
     * @return A [Flow] emitting [NetworkResult] with a list of [Repo].
     */
    fun getRepos(org: String, page: Int): Flow<NetworkResult<List<Repo>>>

    /**
     * Searches repositories across GitHub using a query string.
     *
     * @param query The search query.
     * @param page The page number for pagination.
     * @return A [Flow] emitting [NetworkResult] with a list of [Repo].
     */
    fun searchRepos(query: String, page: Int): Flow<NetworkResult<List<Repo>>>

    /**
     * Fetches a single repository by its unique ID.
     * Returns null if the repository is not found.
     *
     * @param repoId The unique ID of the repository.
     * @return A [Flow] emitting the [Repo] or null.
     */
    fun getRepoById(repoId: Long): Flow<Repo?>   // 👈 new
}
package com.ms.codeatlas.core.data.repository

import com.ms.codeatlas.core.data.local.dao.RepoDao
import com.ms.codeatlas.core.data.mapper.toLocal
import com.ms.codeatlas.core.data.mapper.toRepo
import com.ms.codeatlas.core.data.remote.api.GithubApiService
import com.ms.codeatlas.domain.model.Repo
import com.ms.codeatlas.domain.repository.RepoRepository
import com.ms.codeatlas.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of [RepoRepository] that handles fetching repositories from the GitHub API
 * and caching them locally using Room.
 *
 * @param apiService The GitHub API service for remote calls.
 * @param repoDao DAO for accessing local repository and owner data.
 */
class RepoRepositoryImpl @Inject constructor(
    private val apiService: GithubApiService,
    private val repoDao: RepoDao
) : RepoRepository {

    /**
     * Retrieves a list of repositories for a given organization.
     * First tries to emit cached data if available (for the first page), then fetches from API.
     *
     * @param org GitHub organization name.
     * @param page Page number for pagination.
     * @return A [Flow] emitting [NetworkResult] wrapping a list of [Repo].
     */
    override fun getRepos(org: String, page: Int): Flow<NetworkResult<List<Repo>>> = flow {
        emit(NetworkResult.Loading(true))

        // Try to get cached repos for the first page
        val cachedRepos = if (page == 1) {
            getCachedRepos()
        } else {
            emptyList()
        }

        if (cachedRepos.isNotEmpty() && page == 1) {
            emit(NetworkResult.Success(cachedRepos))
        }

        try {
            val response = apiService.getOrganizationRepos(org, page)
            if (response.isSuccessful) {
                val remoteRepos = response.body() ?: emptyList()

                // Clear and cache the first page
                if (page == 1) {
                    repoDao.clearRepos()
                    repoDao.clearOwners()
                }

                repoDao.insertOwners(remoteRepos.map { it.owner.toLocal() })
                repoDao.insertRepos(remoteRepos.map { it.toLocal() })

                val repos = remoteRepos.map { it.toRepo() }
                emit(NetworkResult.Success(repos))
            } else {
                emit(NetworkResult.Error("HTTP Error: ${response.code()}"))
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("Network error: ${e.message}"))
            // Emit cached data if network fails
            if (cachedRepos.isNotEmpty() && page == 1) {
                emit(NetworkResult.Success(cachedRepos))
            }
        } catch (e: HttpException) {
            emit(NetworkResult.Error("HTTP error: ${e.message}"))
        } finally {
            emit(NetworkResult.Loading(false))
        }
    }

    /**
     * Searches repositories in GitHub using a query string.
     *
     * @param query Search query.
     * @param page Page number for pagination.
     * @return A [Flow] emitting [NetworkResult] wrapping a list of [Repo].
     */
    override fun searchRepos(query: String, page: Int): Flow<NetworkResult<List<Repo>>> = flow {
        emit(NetworkResult.Loading(true))

        try {
            val response = apiService.searchRepos("org:google $query", page)
            if (response.isSuccessful) {
                val searchResponse = response.body()

                searchResponse?.items?.let { repoRemote ->
                    repoDao.insertOwners(repoRemote.map { it.owner.toLocal() })
                    repoDao.insertRepos(repoRemote.map { it.toLocal() })
                }

                val repos = searchResponse?.items?.map { it.toRepo() } ?: emptyList()

                emit(NetworkResult.Success(repos))
            } else {
                emit(NetworkResult.Error("HTTP Error: ${response.code()}"))
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("Network error: ${e.message}"))
        } catch (e: HttpException) {
            emit(NetworkResult.Error("HTTP error: ${e.message}"))
        } finally {
            emit(NetworkResult.Loading(false))
        }
    }

    /**
     * Retrieves a single repository by its ID from local cache.
     *
     * @param repoId Repository ID.
     * @return A [Flow] emitting the [Repo] if found, or null.
     */
    override fun getRepoById(repoId: Long): Flow<Repo?> {
        return repoDao.getRepoById(repoId).map { localRepo ->
            localRepo?.let {
                val owner = repoDao.getOwner(it.ownerId)
                owner?.let { o -> it.toRepo(o) }
            }
        }
    }

    /**
     * Helper function to fetch all cached repositories from the local database.
     *
     * @return List of [Repo] retrieved from local cache.
     */
    private suspend fun getCachedRepos(): List<Repo> {
        // get first emission
        val localReposList = repoDao.getRepos().first()
        val repos = mutableListOf<Repo>()

        for (localRepo in localReposList) {
            val owner = repoDao.getOwner(localRepo.ownerId)
            if (owner != null) {
                repos.add(localRepo.toRepo(owner))
            }
        }
        return repos
    }

}
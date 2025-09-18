package com.ms.codeatlas.domain.usecase

import com.ms.codeatlas.domain.model.Repo
import com.ms.codeatlas.domain.repository.RepoRepository
import com.ms.codeatlas.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching repositories of a GitHub organization.
 *
 * @property repository The [RepoRepository] used to fetch data.
 */
class GetReposUseCase @Inject constructor(
    private val repository: RepoRepository
) {

    /**
     * Fetches a paginated list of repositories for a given organization.
     *
     * @param org The GitHub organization name.
     * @param page The page number for pagination.
     * @return A [Flow] emitting [NetworkResult] with a list of [Repo].
     */
    operator fun invoke(org: String, page: Int): Flow<NetworkResult<List<Repo>>> {
        return repository.getRepos(org, page)
    }
}
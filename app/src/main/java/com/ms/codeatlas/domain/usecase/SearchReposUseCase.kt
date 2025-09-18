package com.ms.codeatlas.domain.usecase

import com.ms.codeatlas.domain.model.Repo
import com.ms.codeatlas.domain.repository.RepoRepository
import com.ms.codeatlas.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching repositories across GitHub.
 *
 * @property repository The [RepoRepository] used to perform search.
 */
class SearchReposUseCase @Inject constructor(
    private val repository: RepoRepository
) {

    /**
     * Searches repositories by a query string.
     *
     * @param query The search query.
     * @param page The page number for pagination.
     * @return A [Flow] emitting [NetworkResult] with a list of [Repo].
     */
    operator fun invoke(query: String, page: Int): Flow<NetworkResult<List<Repo>>> {
        return repository.searchRepos(query, page)
    }
}
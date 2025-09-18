package com.ms.codeatlas.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ms.codeatlas.domain.usecase.GetReposUseCase
import com.ms.codeatlas.domain.usecase.SearchReposUseCase
import com.ms.codeatlas.presentation.common.RepoListEvent
import com.ms.codeatlas.presentation.common.RepoListState
import com.ms.codeatlas.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the repository list screen.
 *
 * Responsibilities:
 * 1. Fetch repositories from the GitHub API using [GetReposUseCase].
 * 2. Search repositories using [SearchReposUseCase].
 * 3. Maintain UI state in [RepoListState], including loading, error, and data.
 * 4. Handle pagination and search query updates.
 */
@HiltViewModel
class RepoListViewModel @Inject constructor(
    private val getReposUseCase: GetReposUseCase,
    private val searchReposUseCase: SearchReposUseCase
) : ViewModel() {

    /** Holds the current search query entered by the user */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /** Holds the current UI state including repo list, loading, and error */
    private val _state = MutableStateFlow(RepoListState())
    val state: StateFlow<RepoListState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        RepoListState()
    )

    /** Tracks the current page for pagination */
    private var currentPage = 1

    /** Determines if more data can be loaded for pagination */
    private var canLoadMore = true

    init {
        observeSearchQuery()
        loadRepos()
    }

    /**
     * Handles events triggered by the UI.
     *
     * @param event The [RepoListEvent] triggered by the user or UI.
     */
    fun onEvent(event: RepoListEvent) {
        when (event) {
            is RepoListEvent.OnSearchQueryChange -> {
                _searchQuery.value = event.query
            }
            is RepoListEvent.LoadNextPage -> {
                if (canLoadMore && !_state.value.isLoading) {
                    currentPage++
                    loadRepos()
                }
            }
            RepoListEvent.Refresh -> {
                currentPage = 1
                loadRepos(refresh = true)
            }
            is RepoListEvent.OnRetry -> {
                loadRepos()
            }
        }
    }

    /** Observes the search query and triggers search after a debounce */
    private fun observeSearchQuery() {
        searchQuery
            .debounce(500) // Wait 500ms before searching to reduce API calls
            .onEach { query ->
                if (query.isNotEmpty()) {
                    searchRepos(query)
                } else {
                    loadRepos()
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Loads repositories from the API or cache.
     *
     * @param refresh If true, resets pagination and clears previous data.
     */
    private fun loadRepos(refresh: Boolean = false) {
        if (refresh) {
            currentPage = 1
            canLoadMore = true
        }

        viewModelScope.launch {
            getReposUseCase("google", currentPage)
                .collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _state.update {
                                it.copy(
                                    isLoading = result.isLoading,
                                    error = null
                                )
                            }
                        }
                        is NetworkResult.Success -> {
                            val currentRepos = if (currentPage == 1) {
                                result.data
                            } else {
                                _state.value.repos + result.data
                            }
                            canLoadMore = result.data.isNotEmpty()
                            _state.update {
                                it.copy(
                                    repos = currentRepos,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is NetworkResult.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
        }
    }

    /**
     * Searches repositories based on the user query.
     *
     * @param query The search string entered by the user.
     */
    private fun searchRepos(query: String) {
        viewModelScope.launch {
            searchReposUseCase(query, 1)
                .collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _state.update {
                                it.copy(
                                    isLoading = result.isLoading,
                                    error = null
                                )
                            }
                        }
                        is NetworkResult.Success -> {
                            _state.update {
                                it.copy(
                                    repos = result.data,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is NetworkResult.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
        }
    }
}
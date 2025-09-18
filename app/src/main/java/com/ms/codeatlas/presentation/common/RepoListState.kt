package com.ms.codeatlas.presentation.common

import com.ms.codeatlas.domain.model.Repo

/**
 * Represents the UI state of the Repo List screen.
 *
 * @property repos The current list of repositories displayed on the screen.
 * @property isLoading Indicates whether data is currently being loaded (initial load or pagination).
 * @property error An optional error message to show if data loading fails.
 */
data class RepoListState(
    val repos: List<Repo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
package com.ms.codeatlas.presentation.common

/**
 * Represents all possible user events or actions in the RepoList screen.
 */
sealed class RepoListEvent {

    /**
     * Triggered when the search query text changes in the search bar.
     *
     * @param query The new search query string.
     */
    data class OnSearchQueryChange(val query: String) : RepoListEvent()

    /**
     * Triggered when the user scrolls near the end of the list,
     * indicating that the next page of results should be loaded.
     */
    data object LoadNextPage : RepoListEvent()

    /**
     * Triggered when the user performs a swipe-to-refresh action,
     * indicating that the list should be reloaded from the first page.
     */
    data object Refresh : RepoListEvent()

    /**
     * Triggered when the user taps the retry button after an error occurs.
     * Typically used to re-attempt the last failed network call.
     */
    data object OnRetry : RepoListEvent()
}
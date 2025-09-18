package com.ms.codeatlas.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ms.codeatlas.domain.repository.RepoRepository
import com.ms.codeatlas.presentation.common.RepoDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the repository details screen.
 *
 * Fetches a single repository by its ID from the repository layer and exposes
 * the UI state as a [StateFlow] of [RepoDetailsState].
 *
 * @property repository The repository layer to fetch repository data.
 * @property savedStateHandle Used to retrieve the repo ID passed via navigation.
 */
@HiltViewModel
class RepoDetailsViewModel @Inject constructor(
    private val repository: RepoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(RepoDetailsState())

    /** Public state exposed to the UI */
    val state: StateFlow<RepoDetailsState> = _state.asStateFlow()

    /** Repository ID retrieved from navigation arguments */
    private val repoId: Long = checkNotNull(savedStateHandle["repoId"])

    init {
        loadRepoDetails()
    }

    /**
     * Loads repository details for the given [repoId].
     *
     * Updates [_state] with either the repository data or an error message if
     * the repository is not found.
     */
    fun loadRepoDetails() {
        viewModelScope.launch {
            repository.getRepoById(repoId).collect { localRepo ->
                if (localRepo != null) {
                    _state.value = RepoDetailsState(repo = localRepo)
                } else {
                    _state.value = RepoDetailsState(error = "Repository not found")
                }
            }
        }
    }
}
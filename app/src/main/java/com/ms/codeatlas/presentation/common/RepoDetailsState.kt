package com.ms.codeatlas.presentation.common

import com.ms.codeatlas.domain.model.Repo

/**
 * Represents the UI state for the repository details screen.
 *
 * @property repo The [Repo] object to display details for. Null if not loaded.
 * @property error Error message to display if fetching the repository details fails. Null if no error.
 */
data class RepoDetailsState(
    val repo: Repo? = null,
    val error: String? = null
)
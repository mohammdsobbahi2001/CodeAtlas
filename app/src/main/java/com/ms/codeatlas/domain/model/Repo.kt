package com.ms.codeatlas.domain.model

/**
 * Represents a GitHub repository in the domain layer.
 *
 * @property id Unique identifier of the repository.
 * @property name Repository name (short name).
 * @property fullName Full repository name including the owner (e.g., "owner/repo").
 * @property owner The [Owner] of the repository.
 * @property htmlUrl URL of the repository on GitHub.
 * @property description Optional description of the repository.
 * @property createdAt Creation date of the repository in ISO 8601 format.
 * @property stargazersCount Number of stars the repository has received.
 */
data class Repo(
    val id: Long,
    val name: String,
    val fullName: String,
    val owner: Owner,
    val htmlUrl: String,
    val description: String?,
    val createdAt: String,
    val stargazersCount: Int
)
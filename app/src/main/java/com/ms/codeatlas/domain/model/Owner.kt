package com.ms.codeatlas.domain.model

/**
 * Represents the owner of a GitHub repository.
 *
 * @property id Unique identifier of the owner.
 * @property login GitHub username of the owner.
 * @property avatarUrl URL of the owner's avatar image.
 */
data class Owner(
    val id: Long,
    val login: String,
    val avatarUrl: String
)
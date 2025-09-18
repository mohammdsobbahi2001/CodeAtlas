package com.ms.codeatlas.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a repository owner stored locally in the Room database.
 *
 * Each [LocalOwner] can be associated with one or more [LocalRepo] entries via a foreign key.
 *
 * @property id Unique identifier of the owner (matches GitHub user ID).
 * @property login Username/login of the owner on GitHub.
 * @property avatarUrl URL of the owner's avatar image.
 */
@Entity(tableName = "owners")
data class LocalOwner(
    @PrimaryKey val id: Long,
    val login: String,
    val avatarUrl: String
)
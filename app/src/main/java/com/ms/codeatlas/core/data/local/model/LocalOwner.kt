package com.ms.codeatlas.core.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents a repository stored locally in the Room database.
 *
 * Each [LocalRepo] is linked to a [LocalOwner] via the [ownerId] foreign key.
 *
 * @property id Unique identifier of the repository.
 * @property name Short name of the repository.
 * @property fullName Full name of the repository, usually in the format "owner/repo".
 * @property ownerId ID of the owner associated with this repository.
 * @property htmlUrl URL to the repository on GitHub.
 * @property description Optional description of the repository.
 * @property createdAt ISO-8601 formatted creation date of the repository.
 * @property stargazersCount Number of stars the repository has.
 */
@Entity(
    tableName = "repos",
    foreignKeys = [
        ForeignKey(
            entity = LocalOwner::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LocalRepo(
    @PrimaryKey val id: Long,
    val name: String,
    val fullName: String,
    val ownerId: Long,
    val htmlUrl: String,
    val description: String?,
    val createdAt: String,
    val stargazersCount: Int,
)
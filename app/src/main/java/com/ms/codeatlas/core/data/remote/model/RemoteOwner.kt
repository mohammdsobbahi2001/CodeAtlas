package com.ms.codeatlas.core.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents the owner of a GitHub repository.
 */
@JsonClass(generateAdapter = true)
data class RemoteOwner(
    val id: Long,
    val login: String,
    @Json(name = "avatar_url") val avatarUrl: String
)
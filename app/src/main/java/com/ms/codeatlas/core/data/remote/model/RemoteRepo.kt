package com.ms.codeatlas.core.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents a GitHub repository returned from the API.
 */
@JsonClass(generateAdapter = true)
data class RemoteRepo(
    val id: Long,
    val name: String,
    @Json(name = "full_name") val fullName: String,
    val owner: RemoteOwner,
    @Json(name = "html_url") val htmlUrl: String,
    val description: String?,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "stargazers_count") val stargazersCount: Int
)

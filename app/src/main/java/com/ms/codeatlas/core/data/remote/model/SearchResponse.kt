package com.ms.codeatlas.core.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response from GitHub repository search endpoint.
 */
@JsonClass(generateAdapter = true)
data class SearchResponse(
    val items: List<RemoteRepo>,
    @Json(name = "total_count") val totalCount: Int,
)
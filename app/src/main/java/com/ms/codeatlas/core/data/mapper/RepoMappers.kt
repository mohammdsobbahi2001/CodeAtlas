package com.ms.codeatlas.core.data.mapper

import com.ms.codeatlas.core.data.local.model.LocalOwner
import com.ms.codeatlas.core.data.local.model.LocalRepo
import com.ms.codeatlas.core.data.remote.model.RemoteOwner
import com.ms.codeatlas.core.data.remote.model.RemoteRepo
import com.ms.codeatlas.domain.model.Owner
import com.ms.codeatlas.domain.model.Repo

/**
 * Converts a [RemoteRepo] object from the network layer into a [LocalRepo] for local database storage.
 */
fun RemoteRepo.toLocal(): LocalRepo {
    return LocalRepo(
        id = id,
        name = name,
        fullName = fullName,
        ownerId = owner.id,
        htmlUrl = htmlUrl,
        description = description,
        createdAt = createdAt,
        stargazersCount = stargazersCount,
    )
}

/**
 * Converts a [RemoteOwner] object from the network layer into a [LocalOwner] for local database storage.
 */
fun RemoteOwner.toLocal(): LocalOwner {
    return LocalOwner(
        id = id,
        login = login,
        avatarUrl = avatarUrl
    )
}

/**
 * Converts a [LocalRepo] object from the database into a domain [Repo] object.
 *
 * @param owner The corresponding [LocalOwner] to associate with the repository.
 */
fun LocalRepo.toRepo(owner: LocalOwner): Repo {
    return Repo(
        id = id,
        name = name,
        fullName = fullName,
        owner = owner.toOwner(),
        htmlUrl = htmlUrl,
        description = description,
        createdAt = createdAt,
        stargazersCount = stargazersCount
    )
}

/**
 * Converts a [LocalOwner] from the database into a domain [Owner] object.
 */
fun LocalOwner.toOwner(): Owner {
    return Owner(
        id = id,
        login = login,
        avatarUrl = avatarUrl
    )
}

/**
 * Converts a [RemoteRepo] directly into a domain [Repo] object.
 */
fun RemoteRepo.toRepo(): Repo {
    return Repo(
        id = id,
        name = name,
        fullName = fullName,
        owner = owner.toOwner(),
        htmlUrl = htmlUrl,
        description = description,
        createdAt = createdAt,
        stargazersCount = stargazersCount
    )
}

/**
 * Converts a [RemoteOwner] directly into a domain [Owner] object.
 */
fun RemoteOwner.toOwner(): Owner {
    return Owner(
        id = id,
        login = login,
        avatarUrl = avatarUrl
    )
}
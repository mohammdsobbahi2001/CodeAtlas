package com.ms.codeatlas.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ms.codeatlas.core.data.local.model.LocalOwner
import com.ms.codeatlas.core.data.local.model.LocalRepo
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for accessing [LocalRepo] and [LocalOwner] entities in the local Room database.
 *
 * This interface provides methods to insert, query, and delete repository and owner data.
 */
@Dao
interface RepoDao {

    /**
     * Inserts a list of repositories into the local database.
     * If a repository with the same ID already exists, it will be replaced.
     *
     * @param repos The list of [LocalRepo] objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<LocalRepo>)

    /**
     * Inserts a list of owners into the local database.
     * If an owner with the same ID already exists, it will be replaced.
     *
     * @param owners The list of [LocalOwner] objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwners(owners: List<LocalOwner>)

    /**
     * Returns a flow of all repositories stored in the local database, ordered by creation date descending.
     *
     * @return A [Flow] emitting a list of [LocalRepo].
     */
    @Query("SELECT * FROM repos ORDER BY createdAt DESC")
    fun getRepos(): Flow<List<LocalRepo>>

    /**
     * Returns a flow containing a single repository with the given ID, or null if not found.
     *
     * @param repoId The ID of the repository to retrieve.
     * @return A [Flow] emitting the [LocalRepo] or null.
     */
    @Query("SELECT * FROM repos WHERE id = :repoId LIMIT 1")
    fun getRepoById(repoId: Long): Flow<LocalRepo?>

    /**
     * Retrieves a single owner by their ID.
     *
     * @param ownerId The ID of the owner to retrieve.
     * @return The [LocalOwner] object if found, or null otherwise.
     */
    @Query("SELECT * FROM owners WHERE id = :ownerId")
    suspend fun getOwner(ownerId: Long): LocalOwner?

    /**
     * Deletes all repositories from the local database.
     */
    @Query("DELETE FROM repos")
    suspend fun clearRepos()

    /**
     * Deletes all owners from the local database.
     */
    @Query("DELETE FROM owners")
    suspend fun clearOwners()

}
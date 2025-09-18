package com.ms.codeatlas.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ms.codeatlas.core.data.local.dao.RepoDao
import com.ms.codeatlas.core.data.local.model.LocalOwner
import com.ms.codeatlas.core.data.local.model.LocalRepo

/**
 * Room database for storing repository and owner data locally.
 *
 * This database includes the [LocalRepo] and [LocalOwner] entities and provides
 * access to the [RepoDao] for performing CRUD operations.
 *
 * Version: 1
 * Schema export: Disabled
 */
@Database(
    entities = [LocalRepo::class, LocalOwner::class],
    version = 1,
    exportSchema = false
)
abstract class RepoDatabase : RoomDatabase() {

    /**
     * Provides access to the [RepoDao] for performing database operations
     * on repositories and owners.
     *
     * @return An instance of [RepoDao].
     */
    abstract fun repoDao(): RepoDao
}
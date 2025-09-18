package com.ms.codeatlas.core.data.repository

import com.ms.codeatlas.core.data.local.dao.RepoDao
import com.ms.codeatlas.core.data.local.model.LocalOwner
import com.ms.codeatlas.core.data.local.model.LocalRepo
import com.ms.codeatlas.core.data.remote.api.GithubApiService
import com.ms.codeatlas.core.data.remote.model.RemoteOwner
import com.ms.codeatlas.core.data.remote.model.RemoteRepo
import com.ms.codeatlas.core.data.remote.model.SearchResponse
import com.ms.codeatlas.domain.model.Owner
import com.ms.codeatlas.domain.model.Repo
import com.ms.codeatlas.util.NetworkResult
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RepoRepositoryImplTest {

    private lateinit var repoDao: RepoDao
    private lateinit var apiService: GithubApiService
    private lateinit var repository: RepoRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    private val localOwner = LocalOwner(
        id = 1L,
        login = "testOwner",
        avatarUrl = "http://avatar.url"
    )

    private val localRepo = LocalRepo(
        id = 100L,
        name = "TestRepo",
        fullName = "testOwner/TestRepo",
        ownerId = localOwner.id,
        htmlUrl = "http://github.com/testOwner/TestRepo",
        description = "A test repo",
        createdAt = "2024-01-01T00:00:00Z",
        stargazersCount = 42
    )

    private val domainRepo = Repo(
        id = localRepo.id,
        name = localRepo.name,
        fullName = localRepo.fullName,
        owner = Owner(
            id = localOwner.id,
            login = localOwner.login,
            avatarUrl = localOwner.avatarUrl
        ),
        htmlUrl = localRepo.htmlUrl,
        description = localRepo.description,
        createdAt = localRepo.createdAt,
        stargazersCount = localRepo.stargazersCount
    )

    private val remoteOwner = RemoteOwner(
        id = 1L,
        login = "testOwner",
        avatarUrl = "http://avatar.url"
    )

    private val remoteRepo = RemoteRepo(
        id = 100L,
        name = "TestRepo",
        fullName = "testOwner/TestRepo",
        owner = remoteOwner,
        htmlUrl = "http://github.com/testOwner/TestRepo",
        description = "A test repo",
        createdAt = "2024-01-01T00:00:00Z",
        stargazersCount = 42
    )

    @Before
    fun setUp() {
        repoDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        repository = RepoRepositoryImpl(apiService, repoDao)

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getRepos emits cached data and API success`() = runTest {
        val org = "google"
        val page = 1

        // Cached data
        coEvery { repoDao.getRepos() } returns flowOf(listOf(localRepo))
        coEvery { repoDao.getOwner(localRepo.ownerId) } returns localOwner

        // API response
        coEvery { apiService.getOrganizationRepos(org, page) } returns Response.success(listOf(remoteRepo))
        coEvery { repoDao.clearRepos() } just Runs
        coEvery { repoDao.clearOwners() } just Runs
        coEvery { repoDao.insertOwners(any()) } just Runs
        coEvery { repoDao.insertRepos(any()) } just Runs

        val results = repository.getRepos(org, page).toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.any { it is NetworkResult.Success && it.data!!.any { r -> r.id == domainRepo.id } })
        assertTrue(results.last() is NetworkResult.Loading)
    }

    @Test
    fun `getRepos emits network error and cached data`() = runTest {
        val page = 1

        coEvery { repoDao.getRepos() } returns flowOf(listOf(localRepo))
        coEvery { repoDao.getOwner(localRepo.ownerId) } returns localOwner
        coEvery { apiService.getOrganizationRepos(any(), any()) } throws IOException("No connection")

        val results = repository.getRepos("google", page).toList()

        assertTrue(results.any { it is NetworkResult.Error })
        assertTrue(results.any { it is NetworkResult.Success && it.data!!.any { r -> r.id == domainRepo.id } })
    }

    @Test
    fun `getRepoById returns repo from dao`() = runTest {
        val repoId = localRepo.id

        coEvery { repoDao.getRepoById(repoId) } returns flowOf(localRepo)
        coEvery { repoDao.getOwner(localRepo.ownerId) } returns localOwner

        val result = repository.getRepoById(repoId).toList()

        assertEquals(domainRepo, result.first())
    }

    @Test
    fun `searchRepos emits API success`() = runTest {
        val query = "compose"
        val page = 1

        coEvery { apiService.searchRepos(any(), page) } returns Response.success(
            SearchResponse(items = listOf(remoteRepo), totalCount = 1)
        )
        coEvery { repoDao.insertOwners(any()) } just Runs
        coEvery { repoDao.insertRepos(any()) } just Runs

        val results = repository.searchRepos(query, page).toList()

        assertTrue(results.any { it is NetworkResult.Success && it.data!!.any { r -> r.id == domainRepo.id } })
        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Loading)
    }
}
package com.ms.codeatlas.domain.usecase

import app.cash.turbine.test
import com.ms.codeatlas.domain.model.Owner
import com.ms.codeatlas.domain.model.Repo
import com.ms.codeatlas.domain.repository.RepoRepository
import com.ms.codeatlas.util.NetworkResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SearchReposUseCaseTest {

        private lateinit var repository: RepoRepository
        private lateinit var searchReposUseCase: SearchReposUseCase

        @Before
        fun setUp() {
            repository = mockk()
            searchReposUseCase = SearchReposUseCase(repository)
        }

        @Test
        fun `invoke should emit success when repository returns repos`() = runTest {
            val query = "compose"
            val page = 1
            val repo = Repo(
                id = 1L,
                name = "ComposeSample",
                fullName = "google/ComposeSample",
                owner = Owner(id = 100, login = "google", avatarUrl = "https://avatar.url"),
                htmlUrl = "https://github.com/google/ComposeSample",
                description = "Sample repo for Jetpack Compose",
                createdAt = "2024-01-01T00:00:00Z",
                stargazersCount = 1234
            )
            val successResult = NetworkResult.Success(listOf(repo))

            // Mock the repository
            coEvery { repository.searchRepos(query, page) } returns kotlinx.coroutines.flow.flow {
                emit(NetworkResult.Loading(true))
                emit(successResult)
                emit(NetworkResult.Loading(false))
            }

            // Collect emissions using Turbine
            searchReposUseCase(query, page).test {
                assertTrue(awaitItem() is NetworkResult.Loading)

                val success = awaitItem()
                assertTrue(success is NetworkResult.Success)
                val repos = (success as NetworkResult.Success).data
                assertEquals(1, repos.size)
                assertEquals("ComposeSample", repos[0].name)

                assertTrue(awaitItem() is NetworkResult.Loading)
                awaitComplete()
            }

            // Verify repository was called
            coVerify(exactly = 1) { repository.searchRepos(query, page) }
        }

        @Test
        fun `invoke should emit error when repository throws exception`() = runTest {
            val query = "compose"
            val page = 1
            val errorMessage = "Network failure"

            coEvery { repository.searchRepos(query, page) } returns kotlinx.coroutines.flow.flow {
                emit(NetworkResult.Loading(true))
                emit(NetworkResult.Error(errorMessage))
                emit(NetworkResult.Loading(false))
            }

            searchReposUseCase(query, page).test {
                assertTrue(awaitItem() is NetworkResult.Loading)

                val error = awaitItem()
                assertTrue(error is NetworkResult.Error)
                assertTrue((error as NetworkResult.Error).message.contains(errorMessage))

                assertTrue(awaitItem() is NetworkResult.Loading)
                awaitComplete()
            }

            coVerify(exactly = 1) { repository.searchRepos(query, page) }
        }

}
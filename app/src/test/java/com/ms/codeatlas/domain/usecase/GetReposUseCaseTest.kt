package com.ms.codeatlas.domain.usecase

import app.cash.turbine.test
import com.ms.codeatlas.domain.model.Owner
import com.ms.codeatlas.domain.model.Repo
import com.ms.codeatlas.domain.repository.RepoRepository
import com.ms.codeatlas.util.NetworkResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RepoRepositoryImplTest {

    private lateinit var repository: RepoRepository
    private lateinit var getReposUseCase: GetReposUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getReposUseCase = GetReposUseCase(repository)
    }

    @Test
    fun `invoke should return repos from repository`() = runTest {
        // Arrange
        val org = "square"
        val page = 1
        val expectedRepo = Repo(
            id = 1L,
            name = "retrofit",
            fullName = "square/retrofit",
            owner = Owner(
                id = 101,
                login = "square",
                avatarUrl = "https://avatars.githubusercontent.com/u/82592?v=4"
            ),
            htmlUrl = "https://github.com/square/retrofit",
            description = "Type-safe HTTP client for Android and Java",
            createdAt = "2013-06-20T14:34:00Z",
            stargazersCount = 45000
        )
        val expectedResult = NetworkResult.Success(listOf(expectedRepo))

        coEvery { repository.getRepos(org, page) } returns flowOf(expectedResult)

        // Act + Assert
        getReposUseCase(org, page).test {
            assert(awaitItem() == expectedResult)
            awaitComplete()
        }

        // Verify repository was called with the correct params
        coVerify(exactly = 1) { repository.getRepos(org, page) }
    }

}
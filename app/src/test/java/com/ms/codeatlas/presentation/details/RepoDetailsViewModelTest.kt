package com.ms.codeatlas.presentation.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ms.codeatlas.domain.model.Owner
import com.ms.codeatlas.domain.model.Repo
import com.ms.codeatlas.domain.repository.RepoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RepoDetailsViewModelTest {

    private lateinit var repository: RepoRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: RepoDetailsViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val repoId = 100L
    private val repo = Repo(
        id = repoId,
        name = "TestRepo",
        fullName = "testOwner/TestRepo",
        owner = Owner(
            id = 1L,
            login = "testOwner",
            avatarUrl = "http://avatar.url"
        ),
        htmlUrl = "http://github.com/testOwner/TestRepo",
        description = "A test repo",
        createdAt = "2024-01-01T00:00:00Z",
        stargazersCount = 42
    )

    @Before
    fun setUp() {
        repository = mockk()
        savedStateHandle = SavedStateHandle(mapOf("repoId" to repoId))
    }

    @Test
    fun `state updates with repo when repository returns data`() = runTest(testDispatcher) {
        // Arrange
        coEvery { repository.getRepoById(repoId) } returns flowOf(repo)

        viewModel = RepoDetailsViewModel(repository, savedStateHandle)

        // Act & Assert
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.repo != null)
            assertEquals(repo.id, state.repo?.id)
            assertEquals(repo.name, state.repo?.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state updates with error when repository returns null`() = runTest(testDispatcher) {
        // Arrange
        coEvery { repository.getRepoById(repoId) } returns flowOf(null)

        viewModel = RepoDetailsViewModel(repository, savedStateHandle)

        // Act & Assert
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.repo == null)
            assertEquals("Repository not found", state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

}
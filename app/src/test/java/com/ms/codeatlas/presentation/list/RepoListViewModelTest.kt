package com.ms.codeatlas.presentation.list

import app.cash.turbine.test
import com.ms.codeatlas.domain.model.Owner
import com.ms.codeatlas.domain.model.Repo
import com.ms.codeatlas.domain.usecase.GetReposUseCase
import com.ms.codeatlas.domain.usecase.SearchReposUseCase
import com.ms.codeatlas.presentation.common.RepoListEvent
import com.ms.codeatlas.util.NetworkResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RepoListViewModelTest {

    private lateinit var getReposUseCase: GetReposUseCase
    private lateinit var searchReposUseCase: SearchReposUseCase
    private lateinit var viewModel: RepoListViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val repo = Repo(
        id = 1L,
        name = "TestRepo",
        fullName = "testOwner/TestRepo",
        owner = Owner(
            id = 101L,
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
        getReposUseCase = mockk()
        searchReposUseCase = mockk()
    }

    @Test
    fun `load repos updates state with success`() = runTest(testDispatcher) {
        coEvery { getReposUseCase("google", 1) } returns flowOf(NetworkResult.Success(listOf(repo)))

        viewModel = RepoListViewModel(getReposUseCase, searchReposUseCase)

        viewModel.state.test {
            val state = awaitItem() // initial state
            assertTrue(state.repos.isEmpty())

            val updatedState = awaitItem() // after loading
            assertEquals(1, updatedState.repos.size)
            assertEquals(repo.id, updatedState.repos[0].id)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { getReposUseCase("google", 1) }
    }

    @Test
    fun `search repos updates state with success`() = runTest(testDispatcher) {
        // Stub initial getReposUseCase call (from init block) with empty list
        coEvery { getReposUseCase("google", 1) } returns flowOf(NetworkResult.Success(emptyList()))

        // Stub searchReposUseCase with the expected repo
        coEvery { searchReposUseCase("compose", 1) } returns flowOf(
            NetworkResult.Success(listOf(repo))
        )

        // Create ViewModel
        viewModel = RepoListViewModel(getReposUseCase, searchReposUseCase)

        // Trigger search
        viewModel.onEvent(RepoListEvent.OnSearchQueryChange("compose"))

        // Await the first state where repos is not empty
        val state = viewModel.state
            .first { it.repos.isNotEmpty() } // suspends until repos is populated

        assertEquals(1, state.repos.size)
        assertEquals(repo.id, state.repos[0].id)

        coVerify { searchReposUseCase("compose", 1) }
    }

    @Test
    fun `load repos updates state with error`() = runTest(testDispatcher) {
        coEvery { getReposUseCase("google", 1) } returns flowOf(NetworkResult.Error("Network error"))

        viewModel = RepoListViewModel(getReposUseCase, searchReposUseCase)

        viewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            assertEquals("Network error", state.error)
            assertTrue(state.repos.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { getReposUseCase("google", 1) }
    }

    @Test
    fun `refresh resets page and reloads repos`() = runTest(testDispatcher) {
        coEvery { getReposUseCase("google", 1) } returns flowOf(NetworkResult.Success(listOf(repo)))

        viewModel = RepoListViewModel(getReposUseCase, searchReposUseCase)
        viewModel.onEvent(RepoListEvent.Refresh)

        coVerify(exactly = 2) { getReposUseCase("google", 1) } // initial + refresh
    }

    @Test
    fun `load next page appends repos`() = runTest(testDispatcher) {
        val repo2 = repo.copy(id = 2L)

        // Mock page 1 and page 2
        coEvery { getReposUseCase("google", 1) } returns flowOf(NetworkResult.Success(listOf(repo)))
        coEvery { getReposUseCase("google", 2) } returns flowOf(NetworkResult.Success(listOf(repo2)))

        // Initialize after mocking
        viewModel = RepoListViewModel(getReposUseCase, searchReposUseCase)

        // Wait for initial page 1 load
        advanceUntilIdle()

        // Trigger loading next page
        viewModel.onEvent(RepoListEvent.LoadNextPage)
        advanceUntilIdle() // wait for page 2 load

        viewModel.state.test {
            skipItems(2) // skip initial Loading and page 1 success
            val state = awaitItem()
            assertEquals(2, state.repos.size)
            assertEquals(repo2.id, state.repos[1].id)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { getReposUseCase("google", 1) }
        coVerify { getReposUseCase("google", 2) }
    }

}

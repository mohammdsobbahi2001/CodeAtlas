package com.ms.codeatlas.presentation.list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ms.codeatlas.R
import com.ms.codeatlas.presentation.common.ErrorScreen
import com.ms.codeatlas.presentation.common.LoadingScreen
import com.ms.codeatlas.presentation.common.RepoListEvent
import com.ms.codeatlas.presentation.list.components.RepoListItem

/**
 * Displays a scrollable list of repositories with search and pagination support.
 *
 * Handles:
 * - Search query updates
 * - Infinite scrolling / load next page
 * - Loading and error states
 *
 * @param navController [NavController] for navigating to repo details.
 * @param viewModel ViewModel providing repo list state and search query.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoListScreen(
    navController: NavController,
    viewModel: RepoListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    // Detect if scrolled near the end to trigger pagination
    val isScrolledToEnd by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index
            lastVisibleItem != null && lastVisibleItem >= totalItems - 5
        }
    }

    LaunchedEffect(isScrolledToEnd) {
        if (isScrolledToEnd && !state.isLoading) {
            viewModel.onEvent(RepoListEvent.LoadNextPage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.google_repos))
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Search input bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { query ->
                        viewModel.onEvent(RepoListEvent.OnSearchQueryChange(query))
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (state.isLoading && state.repos.isEmpty()) {
                    // Show loading when first page is loading
                    LoadingScreen(modifier = Modifier.weight(1f))
                } else if (state.error != null && state.repos.isEmpty()) {
                    // Show error if there is an error and no repos
                    state.error?.let { errorMsg ->
                        ErrorScreen(
                            message = errorMsg,
                            onRetry = { viewModel.onEvent(RepoListEvent.OnRetry) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    // Show list of repositories
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.repos) { repo ->
                            RepoListItem(
                                repo = repo,
                                onClick = {
                                    navController.navigate("repo_details/${repo.id}")
                                }
                            )
                        }

                        // Show loading indicator at the end during pagination
                        if (state.isLoading && state.repos.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * A simple search bar to filter repository list.
 *
 * @param query Current search query string.
 * @param onQueryChange Lambda invoked on query change.
 * @param modifier Optional [Modifier] for styling.
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search)
            )
        },
        placeholder = {
            Text(text = stringResource(R.string.search_repos))
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}
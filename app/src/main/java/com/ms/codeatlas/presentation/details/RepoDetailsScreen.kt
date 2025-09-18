package com.ms.codeatlas.presentation.details

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ms.codeatlas.R
import com.ms.codeatlas.presentation.common.ErrorScreen
import com.ms.codeatlas.util.Utils

/**
 * Screen that displays details of a single repository.
 *
 * Handles loading, error, and success states using the ViewModel.
 *
 * @param navController Navigation controller for handling back navigation.
 * @param viewModel [RepoDetailsViewModel] providing the state for this screen.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoDetailsScreen(
    navController: NavController,
    viewModel: RepoDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.repo_details))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.repo != null -> {
                    state.repo?.let {
                        // Display repository details
                        RepoDetailsContent(repo = it)
                    }

                }

                state.error != null -> {
                    // Show error screen with retry button
                    state.error?.let {
                        ErrorScreen(
                            message = it,
                            onRetry = { viewModel.loadRepoDetails() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                else -> {
                    // Show loading indicator while fetching data
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

/**
 * Displays the content of a repository in a scrollable layout.
 *
 * Shows owner's avatar, login, repository name, description, full name,
 * creation date, and stars count.
 *
 * @param repo The [com.ms.codeatlas.domain.model.Repo] object to display details for.
 */
@Composable
fun RepoDetailsContent(repo: com.ms.codeatlas.domain.model.Repo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Owner info: avatar and login
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    AsyncImage(
                        model = repo.owner.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = repo.owner.login,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = repo.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Repository description (if available)
                repo.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Other repository details
                Text(
                    text = "Full name: ${repo.fullName}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Created: ${Utils.formatDate(repo.createdAt)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Stars: ${repo.stargazersCount}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
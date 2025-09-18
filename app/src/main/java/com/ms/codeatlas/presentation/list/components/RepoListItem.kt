package com.ms.codeatlas.presentation.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ms.codeatlas.domain.model.Repo
import com.ms.codeatlas.util.Utils

/**
 * Displays a single repository item in a list.
 *
 * Shows the owner's avatar, login, repository name, and creation date.
 *
 * @param repo The repository data to display.
 * @param onClick Lambda invoked when the item is clicked.
 * @param modifier Optional [Modifier] for styling.
 */
@Composable
fun RepoListItem(
    repo: Repo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                AsyncImage(
                    model = repo.owner.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = repo.owner.login,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = repo.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Created: ${Utils.formatDate(repo.createdAt)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
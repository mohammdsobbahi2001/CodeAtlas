package com.ms.codeatlas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ms.codeatlas.presentation.details.RepoDetailsScreen
import com.ms.codeatlas.presentation.list.RepoListScreen
import com.ms.codeatlas.presentation.splash.SplashScreen
import com.ms.codeatlas.ui.theme.CodeAtlasTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * MainActivity is the single Activity entry point of the CodeAtlas app.
 * It sets up the app theme, splash screen behavior, and Compose navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // Apply the app theme before anything else
        setTheme(R.style.Theme_CodeAtlas)

        // Determine which splash screen to use based on Android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Use system splash for Android 12+
            val splash = installSplashScreen()
            splash.setKeepOnScreenCondition { true } // keep until Compose content ready

            lifecycleScope.launch {
                delay(3000)
                splash.setKeepOnScreenCondition { false } // remove splash
            }
        }

        super.onCreate(savedInstanceState)

        // Set the Compose content for the activity
        setContent {
            CodeAtlasTheme {
                // Surface container using theme background color
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Use Compose splash only for Android versions < 12
                    GithubReposApp(useComposeSplash = android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S)
                }
            }
        }
    }
}

/**
 * Main Composable function that sets up navigation for the app.
 *
 * @param useComposeSplash Whether to use a custom Compose splash screen
 *                         (true for Android < 12, false otherwise)
 */
@Composable
fun GithubReposApp(useComposeSplash: Boolean) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (useComposeSplash) "splash" else "repo_list"
    ) {
        if (useComposeSplash) {
            // Compose splash screen for older Android versions
            composable("splash") {
                SplashScreen {
                    navController.navigate("repo_list") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
        }

        // Screen displaying the list of repositories
        composable("repo_list") {
            RepoListScreen(navController = navController)
        }

        // Screen displaying repository details
        composable(
            route = "repo_details/{repoId}",
            arguments = listOf(navArgument("repoId") { type = NavType.LongType })
        ) {
            RepoDetailsScreen(navController = navController)
        }
    }
}

/**
 * Preview of the GithubReposApp composable for Android Studio Preview.
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CodeAtlasTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            GithubReposApp(false)
        }
    }
}
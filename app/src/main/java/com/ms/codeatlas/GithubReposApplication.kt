package com.ms.codeatlas

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for CodeAtlas.
 *
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation
 * and allow dependency injection throughout the app.
 */
@HiltAndroidApp
class GithubReposApplication : Application()
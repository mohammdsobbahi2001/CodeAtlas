# GitHub Repos Viewer

A clean Android app that lists Google's GitHub repositories with search, pagination, and detailed views.

## Features

- **Splash Screen** with 3-second timeout
- **Repository List** with real-time search
- **Pagination** for infinite scrolling  
- **Repository Details** with star counts
- **Caching** for offline support
- **Material Design 3** with dark mode

## Tech Stack

- Kotlin with Clean Architecture
- Jetpack Compose UI
- MVVM with StateFlow
- Hilt for Dependency Injection
- Retrofit for Networking
- Room for Caching

## Setup

1. Clone the repository
2. Open in Android Studio
3. Build and run the app

## API

Uses GitHub's public API: `https://api.github.com/orgs/google/repos`

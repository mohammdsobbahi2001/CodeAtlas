package com.ms.codeatlas.util

/**
 * Represents the result of a network request.
 *
 * @param T The type of the successful response data.
 */
sealed class NetworkResult<out T> {

    /**
     * Indicates a successful network request.
     *
     * @param data The response data from the network.
     */
    data class Success<out T>(val data: T) : NetworkResult<T>()

    /**
     * Indicates an error occurred during the network request.
     *
     * @param message A descriptive error message.
     */
    data class Error(val message: String) : NetworkResult<Nothing>()

    /**
     * Indicates that the network request is currently in progress.
     *
     * @param isLoading Boolean flag to indicate loading state.
     */
    data class Loading(val isLoading: Boolean) : NetworkResult<Nothing>()
}
package com.ms.codeatlas.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkResultTest {

    @Test
    fun `NetworkResult Success should contain data`() {
        val testData = "test data"
        val result = NetworkResult.Success(testData)

        assertEquals(testData, result.data)
    }

    @Test
    fun `NetworkResult Error should contain message`() {
        val errorMessage = "Error occurred"
        val result = NetworkResult.Error(errorMessage)

        assertEquals(errorMessage, result.message)
    }

    @Test
    fun `NetworkResult Loading should contain loading state`() {
        val isLoading = true
        val result = NetworkResult.Loading(isLoading)

        assertEquals(isLoading, result.isLoading)
    }
}
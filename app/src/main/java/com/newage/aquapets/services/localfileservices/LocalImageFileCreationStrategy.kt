package com.newage.aquapets.services.localfileservices

import android.content.Context

interface LocalImageFileCreationStrategy {
    val strategyName: String
    fun createImageFile(
        context: Context,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    )
}
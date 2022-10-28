package com.newage.aquapets.services.localfileservices

import android.content.Context

class LocalImageFileCGService(
    private val _localImageFileCreationStrategy: LocalImageFileCreationStrategy
) : ILocalFileCGService {

    override fun createFile(
        context: Context,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        _localImageFileCreationStrategy.createImageFile(context, onSuccess, onError)
    }

    override fun getFile(context: Context, onSuccess: () -> Unit, onError: (e: Exception) -> Unit) {
        TODO("Not yet implemented")
    }


}
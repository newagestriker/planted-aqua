package com.newage.aquapets.services.localfileservices

import android.content.Context

interface ILocalFileCGService {
    fun createFile(
        context: Context,
        onSuccess: () -> Unit,
        onError: (e:Exception) -> Unit
    );

    fun getFile(
        context: Context,
        onSuccess: () -> Unit,
        onError: (e:Exception) -> Unit
    );

}
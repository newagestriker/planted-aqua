package com.newage.plantedaqua.services.localfileservices

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.*
import timber.log.Timber

class LocalImageFileCreationUriCopyStrategy(
    private val _sourceUri: Uri,
    private val _destUri: Uri,
) : LocalImageFileCreationStrategy {
    override val strategyName = "URI_COPY_STRATEGY"
    override fun createImageFile(context: Context, onSuccess: () -> Unit, onError: (e: Exception) -> Unit) {
        Timber.d("Creating New Image")
        try {
            val imageFileCopyJob = SupervisorJob()
            CoroutineScope(Dispatchers.IO + imageFileCopyJob).launch {
                context.applicationContext.contentResolver.openOutputStream(_destUri).use { oStream ->
                    context.applicationContext.contentResolver.openInputStream(_sourceUri)
                        ?.use { iStream -> oStream!!.write(iStream.readBytes()) }
                }
                withContext(Dispatchers.Main) {
                    onSuccess();
                }
            }

        } catch (e: Exception) {
            onError(e)
        }
    }

}
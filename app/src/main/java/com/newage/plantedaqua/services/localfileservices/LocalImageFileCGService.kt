package com.newage.plantedaqua.services.localfileservices

class LocalFileService(private val _directory:String,private val _fileName:String) : ILocalFileService {
    override fun createFile(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteFile(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateFile(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }
}
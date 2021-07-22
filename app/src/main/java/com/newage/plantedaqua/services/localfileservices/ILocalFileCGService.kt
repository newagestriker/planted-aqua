package com.newage.plantedaqua.services.localfileservices

interface ILocalFileService {
    fun createFile(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit);
    fun deleteFile(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit);
    fun updateFile(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit);
}
package com.newage.plantedaqua.services.filewebservices

interface IFileWebService {
    fun fileUpload(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit);
    fun fileDownload(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit);
    fun fileDelete(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit);
}
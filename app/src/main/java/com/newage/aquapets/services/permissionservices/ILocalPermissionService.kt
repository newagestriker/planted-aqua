package com.newage.aquapets.services.permissionservices

interface ILocalPermissionService {
    val permissions: Array<String>
    fun requestPermissions()
    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        onPermissionsGranted: () -> Unit,
        onPermissionsNotGranted: () -> Unit
    )
}

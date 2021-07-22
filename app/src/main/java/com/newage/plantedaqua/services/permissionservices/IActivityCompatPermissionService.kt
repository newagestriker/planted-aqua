package com.newage.plantedaqua.services.permissionservices

interface IActivityCompatPermissionService : ILocalPermissionService {
    val permissionRequestCode:Int
    fun checkPermissions(onPermissionsAvailable:()->Unit,onPermissionsNotAvailable: () -> Unit)
}
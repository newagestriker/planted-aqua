package com.newage.aquapets.services.permissionservices

interface IActivityCompatPermissionService : ILocalPermissionService {
    val permissionRequestCode:Int
    fun checkPermissions(onPermissionsAvailable:()->Unit,onPermissionsNotAvailable: () -> Unit)
}
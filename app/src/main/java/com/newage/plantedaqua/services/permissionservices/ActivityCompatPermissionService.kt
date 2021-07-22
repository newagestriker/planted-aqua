package com.newage.plantedaqua.services.permissionservices

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat



class AndroidXPermissionService(
   override val permissions: Array<String>,
    private val _requestPermissionLauncher: ActivityResultLauncher<String>,
    private val _activity: Activity,
) : IAndroidXPermissionService {



    override fun checkPermissions(
        onPermissionsAvailable: () -> Unit,
        onPermissionsNotAvailable: () -> Unit
    ) {
        when {
            ContextCompat.checkSelfPermission(
                _activity,
                permissions[0]
            ) == PackageManager.PERMISSION_GRANTED -> {
                onPermissionsAvailable()
            }
            shouldShowRequestPermissionRationale(_activity, permissions[0]) -> {
                showPermissionRationale()

            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissions()
            }
        }
    }

    override fun requestPermissions() {

    }

    override fun onRequestPermissionResult(
        onPermissionsGranted: () -> Unit,
        onPermissionsNotGranted: () -> Unit
    ) {
        TODO("Not yet implemented")
    }



    override fun showPermissionRationale() {

    }


}
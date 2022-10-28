package com.newage.aquapets.services.permissionservices

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



class ActivityCompatPermissionService(
    override val permissions: Array<String>,
    private val _activity: Activity, override val permissionRequestCode: Int,
) : IActivityCompatPermissionService {
    override fun checkPermissions(
        onPermissionsAvailable: () -> Unit,
        onPermissionsNotAvailable: () -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            for (i in permissions.indices) {
                if (ContextCompat.checkSelfPermission(
                        _activity.applicationContext,
                        permissions[i]
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    onPermissionsNotAvailable()
                    return
                }
            }
            onPermissionsAvailable()

        }
    }

    override fun requestPermissions() {
        ActivityCompat.requestPermissions(_activity, permissions, permissionRequestCode)
    }

    override fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        onPermissionsGranted: () -> Unit,
        onPermissionsNotGranted: () -> Unit
    ) {
        if (requestCode == permissionRequestCode) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    onPermissionsNotGranted()
                    return
                }
            }
        }
        onPermissionsGranted()
    }
}


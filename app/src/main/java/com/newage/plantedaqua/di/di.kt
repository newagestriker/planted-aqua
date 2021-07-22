package com.newage.plantedaqua.di

import android.app.Activity
import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import com.newage.plantedaqua.repositories.PlantDBRepository
import com.newage.plantedaqua.room.PlantDB
import com.newage.plantedaqua.services.authservices.FireBaseGAuthService
import com.newage.plantedaqua.services.authservices.IAuthService
import com.newage.plantedaqua.services.filewebservices.FirebaseFileWebService
import com.newage.plantedaqua.services.filewebservices.IFileWebService
import com.newage.plantedaqua.services.localfileservices.ILocalFileCGService
import com.newage.plantedaqua.services.localfileservices.LocalImageFileCGService
import com.newage.plantedaqua.services.localfileservices.LocalImageFileCreationStrategy
import com.newage.plantedaqua.services.localfileservices.LocalImageFileCreationUriCopyStrategy
import com.newage.plantedaqua.services.permissionservices.ActivityCompatPermissionService
import com.newage.plantedaqua.services.permissionservices.IActivityCompatPermissionService
import com.newage.plantedaqua.viewmodels.A1ViewModel
import com.newage.plantedaqua.viewmodels.PlantDatabaseActivityViewModel
import com.newage.plantedaqua.viewmodels.TankItemListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dbModules = module {

    factory { PlantDB.getInstance(get()) }
    factory { get<PlantDB>().plantDao }
    single { PlantDBRepository(get()) }

}

val servicesModules = module {
    factory<IFileWebService> { (_firebaseStorageReference: StorageReference, _fileUri: Uri) ->
        FirebaseFileWebService(
            _firebaseStorageReference,
            _fileUri
        )
    }

    factory<IActivityCompatPermissionService> { (
                                                    permissions: Array<String>,
                                                    _activity: Activity, permissionRequestCode: Int
                                                ) ->
        ActivityCompatPermissionService(
            permissions,
            _activity, permissionRequestCode,
        )
    }

    factory<LocalImageFileCreationStrategy> { (
                                                  _sourceUri: Uri,
                                                  _destUri: Uri,
                                              ) ->
        LocalImageFileCreationUriCopyStrategy(
            _sourceUri,
            _destUri,
        )
    }

    factory<ILocalFileCGService> { (_localImageFileCreationStrategy: LocalImageFileCreationStrategy) ->
        LocalImageFileCGService(
            _localImageFileCreationStrategy
        )
    }

    single<IAuthService<FirebaseUser>> { (activity: Activity) -> FireBaseGAuthService(activity) }

}

val vModules = module {
    viewModel { PlantDatabaseActivityViewModel(get(), get()) }
    viewModel { (tankId: String, category: String) ->
        TankItemListViewModel(
            get(),
            tankId,
            category
        )
    }
    viewModel { A1ViewModel(get()) }
}
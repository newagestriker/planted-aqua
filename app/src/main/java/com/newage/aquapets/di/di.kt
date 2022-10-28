package com.newage.aquapets.di

import android.app.Activity
import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import com.newage.aquapets.repositories.PlantDBRepository
import com.newage.aquapets.room.PlantDB
import com.newage.aquapets.services.authservices.FireBaseGAuthService
import com.newage.aquapets.services.authservices.IAuthService
import com.newage.aquapets.services.filewebservices.FirebaseFileWebService
import com.newage.aquapets.services.filewebservices.IFileWebService
import com.newage.aquapets.services.localfileservices.ILocalFileCGService
import com.newage.aquapets.services.localfileservices.LocalImageFileCGService
import com.newage.aquapets.services.localfileservices.LocalImageFileCreationStrategy
import com.newage.aquapets.services.localfileservices.LocalImageFileCreationUriCopyStrategy
import com.newage.aquapets.services.permissionservices.ActivityCompatPermissionService
import com.newage.aquapets.services.permissionservices.IActivityCompatPermissionService
import com.newage.aquapets.viewmodels.A1ViewModel
import com.newage.aquapets.viewmodels.PlantDatabaseActivityViewModel
import com.newage.aquapets.viewmodels.TankItemListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
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
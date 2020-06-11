package com.newage.plantedaqua.di

import com.newage.plantedaqua.fragments.plantdb.PlantDBRepository
import com.newage.plantedaqua.room.PlantDB
import com.newage.plantedaqua.viewmodels.PlantDatabaseActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dbModules =  module{

    factory { PlantDB.getInstance(get()) }
    factory { get<PlantDB>().plantDao }
    single {PlantDBRepository(get(),get())}

}

val vModules = module {
    viewModel { PlantDatabaseActivityViewModel(get()) }
}
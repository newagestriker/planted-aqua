package com.newage.plantedaqua.di

import com.newage.plantedaqua.repositories.PlantDBRepository
import com.newage.plantedaqua.room.PlantDB
import com.newage.plantedaqua.viewmodels.A1ViewModel
import com.newage.plantedaqua.viewmodels.PlantDatabaseActivityViewModel
import com.newage.plantedaqua.viewmodels.TankItemListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dbModules =  module{

    factory { PlantDB.getInstance(get()) }
    factory { get<PlantDB>().plantDao }
    single { PlantDBRepository(get()) }

}

val vModules = module {
    viewModel { PlantDatabaseActivityViewModel(get(),get()) }
    viewModel {(tankId: String,category:String) -> TankItemListViewModel(get(), tankId,category)}
    viewModel { A1ViewModel(get()) }
}
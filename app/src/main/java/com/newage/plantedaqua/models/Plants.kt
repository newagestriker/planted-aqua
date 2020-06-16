package com.newage.plantedaqua.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "aquatic_plants_from_net")
data class Plants(
        @PrimaryKey var plantID : Long = 0L,
        var commonName : String = "",
        var scientificName : String = "",
        var plantDifficulty : String = "",
        var plantLightRegion : String = "",
        var plantTankPlacement : String = "",
        var plantDominantColor : String = "",
        var plantTemperature : String = "",
        var plantPointsToNote : String = "",
        var plantPicUri : String = "",
        var plantAuthorName : String = "",
        var plantPicAuthorLink : String = "",
        var plantPicLicenceLink : String = "",
        var plantPicLicenceName : String = "",
        var plantCategory : String = "",
        var plantGrowthRate : String = "",
        var plantSize : String = "",
        var plantCO2Demand : String = "",
        var plantPh : String = "",
        var LightCategory: String = ""


) : Parcelable
package com.newage.plantedaqua.models

data class TanksDetails(
        var tankID : String = "",
        var tankName : String = "",
        var tankPicUri : String = "",
        var tankType : String = "",
        var tankStatus : String = "",
        var tankLightRegion : String = "",
        var tankCO2Supply : String = "",
        var tankStartDate : String = "",
        var tankEndDate : String = ""
) {
}
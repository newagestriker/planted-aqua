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
        var tankEndDate : String = "",
        var macroDosageText : String = "",
        var microDosageText : String = "",
        var cumExpenses : String = "",
        var tankVolume : String = "",
        var tankVolumeMetric : String = "",
        var tankPrice : String = "",
        var currency: String = ""
) {
}
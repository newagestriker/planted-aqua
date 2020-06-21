package com.newage.plantedaqua.models
//TankItems(I_ID text,I_Name text,I_Category text,I_URI text,I_Currency text,I_Price real,I_Quantity integer,I_BuyDate text,I_ExpDate text,I_Gender text,I_Food text,I_Care text,I_Quality text,I_Remarks text,I_Sci_Name text,I_Seller text)");
data class TankItems(

        var itemUri: String = "",
        var txt1: String = "",
        var txt2: String = "",
        var txt3: String = "",
        var txt4: String = "",
        var tag: String = "",
        var checked: Boolean = false,
        var quickNote: String = "",
        var shown: Boolean = false,
        var itemName: String = "",
        var itemCategory: String = "",
        var Currency: String = "",
        var itemPrice: String = "0.0",
        var itemQuantity: String = "1",
        var itemPurchaseDate: String = "",
        var itemExpiryDate: String = "",
        var itemGender : String = "",
        var food: String = "",
        var itemCare: String = "",
        var itemQuality: String = "1",
        var itemRemarks : String = "",
        var itemSeller : String ="",
        var itemScientificName : String = "",
        var mode : String = "creation"
){

}

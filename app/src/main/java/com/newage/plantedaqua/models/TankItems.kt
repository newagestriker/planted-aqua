package com.newage.plantedaqua.models

import java.util.*

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

    fun getNumericPrice() = (if(itemPrice.isBlank()) 0.0f else itemPrice.replace(",",".").toFloat())
    fun getNumericQuantity() = (if(itemQuantity.isBlank()) 1 else itemQuantity.toInt())
    fun String.returnOneIfBlank() = if(this.isBlank()) "1" else this
    fun String.returnZeroIfBlank() = if(this.isBlank()) "0.0" else this

    fun getTotalPrice(quantity : Int, price : Float) = String.format(Locale.getDefault(),"%.2f",(quantity*price))
}

package com.newage.plantedaqua.models

import com.newage.plantedaqua.constants.SELLER

class Seller : User {
    companion object{
        final const val type = SELLER
    }
    override var userName: String = ""
    override var email: String = ""
    override var phoneNumber: String = ""
    override var photoUrl: String = ""
    var speciality: String = ""
    var contact: String = ""
    var fixLocation : Boolean = false

}
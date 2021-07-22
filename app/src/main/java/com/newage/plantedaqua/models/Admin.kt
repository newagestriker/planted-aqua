package com.newage.plantedaqua.models

import com.newage.plantedaqua.constants.ADMIN

class Admin : User {
    companion object{
        final const val type = ADMIN
    }
    override var userName: String = ""
    override var email: String = ""
    override var phoneNumber: String = ""
    override var photoUrl: String = ""
    var fixLocation : Boolean = false
}
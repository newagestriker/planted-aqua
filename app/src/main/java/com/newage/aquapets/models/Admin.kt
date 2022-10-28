package com.newage.aquapets.models

import com.newage.aquapets.constants.ADMIN

class Admin : User {
    companion object{
        final const val type = ADMIN
    }

    override var userName: String = ""
    override var email: String = ""
    override var phoneNumber: String? = null
    override var photoUrl: String? = null
    var fixLocation : Boolean = false
}
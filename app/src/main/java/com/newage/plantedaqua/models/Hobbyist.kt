package com.newage.plantedaqua.models

import com.newage.plantedaqua.constants.HOBBYIST

class Hobbyist : User {
    companion object{
        final const val type = HOBBYIST
    }
    override var userName: String = ""
    override var email: String = ""
    override var phoneNumber: String = ""
    override var photoUrl: String = ""
    var fixLocation : Boolean = false
}
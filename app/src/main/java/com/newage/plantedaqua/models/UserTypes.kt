package com.newage.plantedaqua.models

class UserTypes {

    companion object {
        @Volatile
        private var instance: UserTypes? = null

        fun getInstance(): UserTypes {
            synchronized(this) {
                if (instance == null) {
                    return UserTypes()
                }
                return instance as UserTypes
            }
        }

    }

    private val userTypes: ArrayList<String> = ArrayList()

    fun addUserTypes(userType: Array<String>) {
        userTypes.addAll(userType)
    }

    fun getUserTypes() = userTypes
}
package com.newage.aquapets.services.authservices

import android.content.Intent
import androidx.lifecycle.LiveData

interface IAuthService<T> {

    var user :T?
    fun signIn()
    fun signOut()
    fun hasLoggedOut():LiveData<Boolean>
    var error:LiveData<String?>
    fun onSignInSuccess(data: Intent?)

}
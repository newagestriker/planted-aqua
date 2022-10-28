package com.newage.aquapets.services.authservices


import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.newage.aquapets.R
import timber.log.Timber
import java.util.*


class FireBaseGAuthService(private val activity: Activity) : IAuthService<FirebaseUser> {

    companion object {
        private const val RC_SIGN_IN = 47
    }

    override var user: FirebaseUser? = null
    override fun hasLoggedOut(): LiveData<Boolean> = hasLoggedOut
    private var firebaseError = MutableLiveData<String?>()
    override var error: LiveData<String?> = firebaseError

    private var mGoogleSignInClient: GoogleSignInClient
    private var hasLoggedOut = MutableLiveData<Boolean>()
    val mAuth = FirebaseAuth.getInstance()

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.applicationContext.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)

        user = mAuth.currentUser // To add user data if user is already logged in

    }

    override fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    mAuth.currentUser?.let {
                        user = it
                        hasLoggedOut.value = false
                    }
                    // progressDialog.dismiss();
                    firebaseError.value = null
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.e("Firebase Error %s", task.exception?.message!!);
                    firebaseError.value = task.exception?.message!!
                    try {
                        throw Objects.requireNonNull(task.exception)!!
                    } catch (e: Exception) {
                        Timber.e("Firebase Error %s", e.message!!);
                        firebaseError.value = e.message
                    }

                    user = null
                }

                // ...
            }
    }

    override fun signOut() {
        mAuth.signOut()
        hasLoggedOut.value = true
    }

    override fun onSignInSuccess(data:Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)

        } catch (e: ApiException) {
            firebaseError.value = e.message
            // progressDialog.cancel();
        }
    }


}
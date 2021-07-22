package com.newage.plantedaqua.helpers


import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.newage.plantedaqua.R
import timber.log.Timber
import java.util.*


class FireBaseGAuthHelper(private val activity: Activity) {

    companion object {
        private const val RC_SIGN_IN = 47
    }

    private var mGoogleSignInClient: GoogleSignInClient
    private var userLiveData = MutableLiveData<FirebaseUser?>()
    private val mAuth = FirebaseAuth.getInstance()

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.applicationContext.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)

        val user = mAuth.currentUser
        user?.let {
            userLiveData.value = it
        }
    }

    private fun signIn() {
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
                        userLiveData.value = it
                    }
                    // progressDialog.dismiss();

                } else {
                    // If sign in fails, display a message to the user.
                    Timber.e("Firebase Error " + task.exception?.message!!);
                    try {
                        throw Objects.requireNonNull(task.exception)!!
                    } catch (e: Exception) {
                        Timber.e("Firebase Error " + e.message!!);
                    }
                    userLiveData.value = null
                }

                // ...
            }
    }


}
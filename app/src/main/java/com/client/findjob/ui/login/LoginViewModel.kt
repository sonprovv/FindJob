package com.client.findjob.ui.login

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.client.findjob.base.BaseViewModel
import com.client.findjob.data.model.User
import com.client.findjob.utils.FirebaseUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val firebaseUtils: FirebaseUtils
) : BaseViewModel(application) {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private lateinit var googleSignInClient: GoogleSignInClient

    init {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getGoogleServerClientId())
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(application, gso)
    }

    private fun getGoogleServerClientId(): String = Utils.WEB_CLIENT_ID

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(data: Intent?, onSuccess: () -> Unit) {
        launch {
            try {
                _isLoading.postValue(true)
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!, onSuccess)
            } catch (e: ApiException) {
                _error.postValue("Google sign in failed: ${e.statusCode}")
                _isLoading.postValue(false)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onSuccess: () -> Unit) {
        launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseUtils.auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val firebaseUser = firebaseUtils.auth.currentUser
                            firebaseUser?.let { user ->
                                saveUserToDatabase(user)
                                onSuccess()
                            }
                        } else {
                            _error.postValue("Authentication failed: ${task.exception?.message}")
                        }
                        _isLoading.postValue(false)
                    }
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Authentication failed")
                _isLoading.postValue(false)
            }
        }
    }

    private fun saveUserToDatabase(user: com.google.firebase.auth.FirebaseUser) {
        val userRef = firebaseUtils.usersReference.child(user.uid)

        val userData = hashMapOf(
            "id" to user.uid,
            "name" to user.displayName,
            "email" to user.email,
            "photoUrl" to user.photoUrl?.toString()
        )

        userRef.setValue(userData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _user.postValue(
                    User(
                        id = user.uid,
                        name = user.displayName ?: "",
                        email = user.email ?: "",
                        photoUrl = user.photoUrl?.toString() ?: ""
                    )
                )
            } else {
                _error.postValue("Failed to save user data: ${task.exception?.message}")
            }
        }
    }

    fun signOut() {
        firebaseUtils.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            _user.postValue(null)
        }
    }

    fun getCurrentUser() {
        val currentUser = firebaseUtils.auth.currentUser
        _user.postValue(
            currentUser?.let { user ->
                User(
                    id = user.uid,
                    name = user.displayName ?: "",
                    email = user.email ?: "",
                    photoUrl = user.photoUrl?.toString() ?: ""
                )
            }
        )
    }
}
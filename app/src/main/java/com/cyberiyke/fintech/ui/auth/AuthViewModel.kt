package com.cyberiyke.fintech.ui.auth

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cyberiyke.fintech.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.iyke.onlinebanking.utils.Constants
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                showLoading()
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                saveUserDataToSharedPreferences(email, "null", "null")
                navigateToMainActivity()
            } catch (e: Exception) {
                showToast("Login Failure: ${e.message}")
            } finally {
                hideLoading()
            }
        }
    }

    fun registerWithEmailAndPassword(email: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                showLoading()
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                saveUserDataToSharedPreferences(email, name, "null")
                navigateToVerifyPhoneNumber()
            } catch (e: Exception) {
                showToast("Registration Failure: ${e.message}")
            } finally {
                hideLoading()
            }
        }
    }

    private fun saveUserDataToSharedPreferences(email: String, name: String, profilePic: String) {
        val preferences = context.getSharedPreferences(Constants.PREFERENCE, MODE_PRIVATE)
        preferences.edit().apply {
            putString(Constants.EMAIL, email)
            putString(Constants.NAME, name)
            putString(Constants.PROFILE, profilePic)
            apply()
        }
    }

    fun firebaseLogin(idToken: String) {
        viewModelScope.launch {
            try {
                showLoading()
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(credential).await()
                val user = firebaseAuth.currentUser
                if (user != null) {
                    val userDoc = FirebaseFirestore.getInstance()
                        .collection(Constants.USERS)
                        .document(user.email!!)
                        .get()
                        .await()

                    val name = user.displayName ?: "null"
                    val profileUri = user.photoUrl?.toString() ?: "null"
                    saveUserDataToSharedPreferences(user.email!!, name, profileUri)

                    if (userDoc.exists()) {
                        navigateToMainActivity()
                    } else {
                        navigateToVerifyPhoneNumber()
                    }
                }
            } catch (e: Exception) {
                showToast("Login failed: ${e.message}")
                Log.e("AuthViewModel", "Firebase login failed", e)
            } finally {
                hideLoading()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun navigateToVerifyPhoneNumber() {
        // TODO: Add phone number verification for extra security

    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading() {
        // Implement a loading indicator here, e.g., using a LiveData to communicate with the UI
    }

    private fun hideLoading() {
        // Hide the loading indicator here
    }
}

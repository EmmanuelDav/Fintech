package com.cyberiyke.fintech.ui.auth

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cyberiyke.fintech.ui.MainActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iyke.onlinebanking.utils.Constants
import com.iyke.onlinebanking.utils.Constants.BALANCE
import com.iyke.onlinebanking.utils.Constants.EMAIL
import com.iyke.onlinebanking.utils.Constants.NAME
import com.iyke.onlinebanking.utils.Constants.PHONE_NUMBER
import com.iyke.onlinebanking.utils.Constants.PIN
import com.iyke.onlinebanking.utils.Constants.PREFERENCE
import com.iyke.onlinebanking.utils.Constants.PROFILE
import com.iyke.onlinebanking.utils.Constants.USERS
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import timber.log.Timber

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

    fun registerWithEmailAndPassword(email: String, password: String, name: String, view: View) {

        val progressBar = CircularProgressIndicator(view.context).apply {
            isIndeterminate = true
            visibility = View.VISIBLE
        }
        (view as? ViewGroup)?.addView(progressBar)

        viewModelScope.launch {
            try {
                showLoading()
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                    saveUserDataToSharedPreferences(email, name, "null")
                    saveDataToFirebase(progressBar)
                }
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


    private fun navigateToMainActivity() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun saveDataToFirebase(progressIndicator: CircularProgressIndicator) {

        val sh = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        val email = sh.getString(EMAIL, "")
        val name = sh.getString(NAME, "")
        val profilePic = sh.getString(PROFILE, "")

        val userDocument = FirebaseFirestore.getInstance().collection(USERS).document(email!!)


        val data = hashMapOf(
            EMAIL to email,
            NAME to name,
            PROFILE to profilePic,
            PHONE_NUMBER to "null",
            BALANCE to null,
            PIN to null
        )

// Set data and update balance if null, then navigate to MainActivity
        userDocument.set(data)
            .addOnSuccessListener {
                userDocument.get()
                    .addOnSuccessListener { doc ->
                        if (doc[BALANCE] == null) {
                            userDocument.update(BALANCE, 1000)
                        }
                        progressIndicator.visibility = View.GONE
                        // Navigate to MainActivity
                        Intent(context, MainActivity::class.java).let {
                            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(it)
                        }
                        Timber.tag("VerifyActivity").d("signInWithCredential:success")
                    }
                    .addOnFailureListener {
                        progressIndicator.visibility = View.GONE
                        Timber.tag("VerifyActivity").d("Log in failed because ${it.message}")
                    }
            }
            .addOnFailureListener { e ->
                Timber.tag("TAG").w(e, "Error writing document")
            }

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

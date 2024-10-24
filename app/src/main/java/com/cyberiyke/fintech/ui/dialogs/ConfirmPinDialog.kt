package com.cyberiyke.fintech.ui.dialogs


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cyberiyke.fintech.R
import com.cyberiyke.fintech.databinding.DialogConfirmPinBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.iyke.onlinebanking.utils.Constants.EMAIL
import com.iyke.onlinebanking.utils.Constants.PREFERENCE

class ConfirmPinDialog(private val activity: Context) : Dialog(activity) {
    var confirmed = false


    lateinit var binding:DialogConfirmPinBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogConfirmPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        setOnCancelListener {
            dismiss()
        }

        // Retrieve SharedPreferences outside of the listener
        val sharedPreferences =
            context.getSharedPreferences(PREFERENCE, AppCompatActivity.MODE_PRIVATE)
        val firebaseEmail = sharedPreferences.getString(EMAIL, null) ?: return

        binding.buttonConfirmDialogPin.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                     binding.buttonConfirmDialogPin.setBackgroundResource(R.drawable.icon_menu_bg_custom_2)
                }

                MotionEvent.ACTION_UP -> {
                    binding.progressBarConfirmPin.setBackgroundResource(R.drawable.button_bg_custom)

                    if (!isPinValid()) return@setOnTouchListener true

                    binding.progressBarConfirmPin.visibility = View.VISIBLE

                    checkUserPin(firebaseEmail) { isPinCorrect ->
                        binding.progressBarConfirmPin.visibility = View.INVISIBLE
                        if (isPinCorrect) {
                           confirmed = true
                            dismiss()
                        } else {
                            binding.editTextDialogBoxPin.error = "Incorrect PIN"
                        }
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

        // Helper function to validate PIN length
        private fun isPinValid(): Boolean {
            return if (binding.editTextDialogBoxPin.length() < 6) {
                binding.editTextDialogBoxPin.error = "Insert 6-digit PIN"
                false
            } else {
                true
            }
        }


        // Function to check if the user PIN matches
        private fun checkUserPin(email: String, callback: (Boolean) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("users").document(email)

            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document["pin"] == binding.editTextDialogBoxPin.text.toString()) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("ConfirmPinActivity", "Fetching document failed", exception)
                    binding.editTextDialogBoxPin.error = "Failed to verify"
                    callback(false)
                }
        }



}

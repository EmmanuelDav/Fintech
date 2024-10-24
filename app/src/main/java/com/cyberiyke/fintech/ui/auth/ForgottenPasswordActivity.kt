package com.cyberiyke.fintech.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cyberiyke.fintech.databinding.ActivityForgottenPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgottenPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgottenPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgottenPasswordBinding.inflate(layoutInflater)

        binding.verify.setOnClickListener {
            val email: String = binding.emailInp.text.toString().trim { it <= ' ' }

            if (email.isEmpty()) {
                Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email sent Successfully", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                this,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}
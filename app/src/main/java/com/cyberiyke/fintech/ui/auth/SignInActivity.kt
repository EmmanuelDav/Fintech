package com.cyberiyke.fintech.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.cyberiyke.fintech.R
import com.cyberiyke.fintech.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    lateinit var authViewModel: AuthViewModel
    lateinit var binding: ActivitySignInBinding
    lateinit var dialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)

        setContentView(binding.root)
         dialog = AlertDialog.Builder(this)
            .setView(R.layout.load_dialog)
            .setCancelable(false)
            .create()




        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.signUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))

        }
        binding.signIn.setOnClickListener {

            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()


            if (email.isEmpty()) {
                binding.emailInput.error = "Please Enter Email"
                binding.emailInput.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailInput.error = "Please Enter Valid Email"
               binding.emailInput.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.passwordInput.error = "Please Enter Password"
                binding.passwordInput.requestFocus()
                return@setOnClickListener
            }


            if (email.isNotEmpty() && password.isNotEmpty()) {
                dialog.show()
                authViewModel.loginWithEmailAndPassword(email, password, dialog)
            }
        }

        binding.forPassword.setOnClickListener {
            startActivity(Intent(this, ForgottenPasswordActivity::class.java))
        }
    }
}
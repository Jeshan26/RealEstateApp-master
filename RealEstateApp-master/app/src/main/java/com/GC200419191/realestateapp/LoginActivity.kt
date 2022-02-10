package com.GC200419191.realestateapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.GC200419191.realestateapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityLoginBinding
    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress Dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Making sure that user waits while the account is created in backend
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //Register Click
        binding.noAccountTv.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //Login Click
        binding.loginBtn.setOnClickListener{
            validateData()
        }

        binding.googleLoginBtn.setOnClickListener {
            startActivity(Intent(this, GoogleSignIn::class.java))
        }
    }
    private var email = ""
    private var password = ""

    private fun validateData() {
        //Getting the inputs from the user
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        // Data validations
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Email is either Invalid or Empty!!", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()){
            Toast.makeText(this,"Enter your Password", Toast.LENGTH_SHORT).show()
        }
        else{
            loginUser()
        }
    }

    //This function will log User into the System
    private fun loginUser() {
        progressDialog.setMessage("Logging In....")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this,"Login Credentials Verified!!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                progressDialog.dismiss()
            }
            .addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(this,"LogIn Failed reason being ${it.message}!!", Toast.LENGTH_SHORT).show()
            }
    }


}
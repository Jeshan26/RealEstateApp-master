package com.GC200419191.realestateapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.GC200419191.realestateapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {


    //view binding
    private lateinit var binding: ActivityRegisterBinding

    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress Dialog
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Making sure that user waits while the account is created in backend
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handling the back button
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

        //handling the register button
        binding.registerBtn.setOnClickListener{
            validateData()
        }

    }


    private var name = ""
    private var email = ""
    private fun validateData() {
        // Initializing the user input in to global variables
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        cPassword = binding.cPasswordEt.text.toString().trim()

        // Validating the user information to make sure that database have right information in it
        if(name.isEmpty()){
            Toast.makeText(this,"Enter your name please!!", Toast.LENGTH_SHORT).show()
        }
        else if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Email is either Invalid or Empty!!", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()){
            Toast.makeText(this,"Enter your Password", Toast.LENGTH_SHORT).show()
        }
        else if(cPassword.isEmpty()){
            Toast.makeText(this,"Please confirm your Password!!", Toast.LENGTH_SHORT).show()
        }
        else if(password != cPassword){
            Toast.makeText(this,"Password Miss Match", Toast.LENGTH_SHORT).show()
        }
        else{
            createUserAccount()
        }


    }

    private fun createUserAccount() {
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // saving information in RealTime Database
                updateUserInfo()
            }
            .addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(this,"Account Not Created!!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        progressDialog.setMessage("Saving User info...")

        val timestamp = System.currentTimeMillis()

        val uid = firebaseAuth.uid

        //storing the information of user
        val hashmap: HashMap<String, Any?> = HashMap()
        hashmap["uid"] = uid
        hashmap["email"] = email
        hashmap["name"] = name
        hashmap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashmap)
            .addOnSuccessListener {
                // here the user information is saved just redirect the user to other activity
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(this,"Information not saved!!", Toast.LENGTH_SHORT).show()
            }
    }
    private var password = ""
    private var cPassword = ""
}
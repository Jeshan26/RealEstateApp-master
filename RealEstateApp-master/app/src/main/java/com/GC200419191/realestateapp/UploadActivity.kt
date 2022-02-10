package com.GC200419191.realestateapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.GC200419191.realestateapp.databinding.ActivityUploadBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UploadActivity : AppCompatActivity() {

    // binding
    private lateinit var binding: ActivityUploadBinding
    // progress Dialog
    private lateinit var progressDialog: ProgressDialog
    // Image URL
    private lateinit var filepath: Uri

    val property = Property()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //this line of code will bring all the ids from xml
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.select.setOnClickListener{
            startFlieChooser()
        }
        binding.upload.setOnClickListener{
            uploadFile()
        }

        // Validating the user responses
        binding.button.setOnClickListener{

            if(binding.addressEditText.text.toString().isNotEmpty() && binding.postalCodeEditText.text.toString().isNotEmpty()
                && binding.cityEditText.text.toString().isNotEmpty()){

                // Making sure that user waits while the account is created in backend
                progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Saving your information")
                progressDialog.setCanceledOnTouchOutside(false)


                //creating the instance of the Property

                property.address = binding.addressEditText.text.toString()
                property.postalCode = binding.postalCodeEditText.text.toString()
                property.city = binding.cityEditText.text.toString()
                property.bed =  binding.spinner.selectedItem.toString().toInt()
                property.room =  binding.spinnerRoom.selectedItem.toString().toInt()
                property.bath =  binding.spinnerBath.selectedItem.toString().toInt()

                // Saving your information
                progressDialog.setMessage("Your Property is now listed...")
                progressDialog.show()

                //Storing the restaurant in Firebase - Store
                //1. get an ID from Firestore(Firestore only does the storing and retrieving
                val db = FirebaseFirestore.getInstance().collection("properties")
                property.id = db.document().id

                //2. store the restaurant as a document - in MySql document can be considered as row
                // !! this is null protection built by kotlin
                db.document(property.id!!).set(property)
                    //adding the listeners to make sure user see something on screen when the row is added to data
                    .addOnSuccessListener {

                        Toast.makeText(this,"Property Added", Toast.LENGTH_LONG).show()
                        binding.addressEditText.setText("")
                        binding.postalCodeEditText.setText("")
                        binding.cityEditText.setText("")
                        binding.spinner.setSelection(0)
                        binding.spinnerRoom.setSelection(0)
                        binding.spinnerBath.setSelection(0)

                        // Removing the Dialog box
                        progressDialog.dismiss()
                        //startActivity(Intent(this, GridRecyclerActivity::class.java))
                    }
                    //listener to make sure that in case of failure user still see messages
                    .addOnFailureListener{
                        Toast.makeText(this,"Restaurant name and rating required", Toast.LENGTH_LONG).show()
                        var message = it.localizedMessage
                        message.let{
                            Log.i("DB Message", message)
                        }
                    }
            }
            else{
                Toast.makeText(this,"Every field is required", Toast.LENGTH_LONG).show()
            }
        }
        setSupportActionBar(binding.mainToolBar.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.ic_home ->{
                startActivity(Intent(applicationContext, MainActivity::class.java))
                return true
            }
            R.id.action_profile ->{
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
            R.id.ic_create ->{
                //startActivity(Intent(applicationContext, UploadActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadFile() {
        if(filepath!=null){
            var pd = ProgressDialog(this)
            pd.setTitle("Uploading...")
            pd.show()

            var randomKey = UUID.randomUUID().toString()

            var imageRef = FirebaseStorage.getInstance().getReference().child("images/"+binding.addressEditText.text.toString()+".jpg")
            imageRef.putFile(filepath)
                .addOnSuccessListener {
                    pd.dismiss()
                    Toast.makeText(this,"Images Uploaded", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener{
                    pd.dismiss()
                    Toast.makeText(this,"Failed to upload the Image. Please try again!!", Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { p0->
                    var progress = (100.00 * p0.bytesTransferred)/ p0.totalByteCount
                    pd.setMessage("${progress.toInt()}% Uploaded!!")
                }

        }
    }

    private fun startFlieChooser() {
        var i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i,"Choose Picture"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==111 && resultCode == Activity.RESULT_OK && data != null){
            filepath = data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filepath)
            binding.imageView.setImageBitmap(bitmap)
        }
    }
}
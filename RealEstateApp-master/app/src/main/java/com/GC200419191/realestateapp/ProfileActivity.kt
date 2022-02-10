package com.GC200419191.realestateapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.GC200419191.realestateapp.databinding.ActivityProfileBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val authDB = FirebaseAuth.getInstance()


    // define variables to use with camera
    private val REQUEST_CODE = 1000
    private lateinit var filePhoto : File
    private val FILE_NAME = "photo"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //enabling the scrollbars
        binding.termsTextView.movementMethod = ScrollingMovementMethod()

        //ensure we have the authenticated user
        if(authDB.currentUser == null)
            logout()
        else{
            // ?.let make sure the input is always not null
            authDB.currentUser?.let { user ->
                binding.userNameTextView.text = user.displayName
                binding.emailTextView.text = user.email
                var profileUrl = user.photoUrl
                try{
                    if(profileUrl != null && profileUrl.path != null)
                        loadProfileImage(profileUrl.path!!)
                }
                catch(e: Exception){
                    Toast.makeText(this, "Image not found in database", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // loggin the user out
        binding.floatingActionButton.setOnClickListener{
            logout()
        }

        binding.cameraButton.setOnClickListener {
            //asking for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.CAMERA,
                        Manifest.permission.CAMERA,
                    ),
                    1
                )

            }

            //now setting up the photo
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            filePhoto = getPhotoFile(FILE_NAME)

            val providerFile = FileProvider.getUriForFile(this, "com.GC200419191.realestateapp.fileProvider", filePhoto)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)

            if(intent.resolveActivity(this.packageManager) != null)
                startActivityForResult(intent, REQUEST_CODE)
            else
                Toast.makeText(this, "Camera did not open", Toast.LENGTH_SHORT).show()
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
                //startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
            R.id.ic_create ->{
                startActivity(Intent(applicationContext, UploadActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        authDB.signOut()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun getPhotoFile(fileName: String) : File{
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode ==REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val takenPhoto = BitmapFactory.decodeFile(filePhoto.absolutePath)
            binding.avatarImageView.setImageBitmap(takenPhoto)

            val builder = Uri.Builder()
            val localUri = builder.appendPath(filePhoto.absolutePath).build()
            saveProfilePhoto(localUri)

        }

        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun saveProfilePhoto(localUri: Uri?) {
        var profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(localUri)
            .build()
        authDB.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener{
            OnCompleteListener<Void?>{
                if(it.isSuccessful){
                    Toast.makeText(this, "Profile Update", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Profile Update Failed!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadProfileImage(pathToImage: String){
        val file: File = File(pathToImage)
        var bitmapImage = BitmapFactory.decodeStream(FileInputStream(file))
        binding.avatarImageView.setImageBitmap(bitmapImage)
    }
}

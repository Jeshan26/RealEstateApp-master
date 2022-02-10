package com.GC200419191.realestateapp

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.GC200419191.realestateapp.databinding.ActivityDescriptionBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class DescriptionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater) 
        setContentView(binding.root)

        // intent is going to make sure that we get the ids from previous xml
        binding.address.text = intent.getStringExtra("propertyAddress")
        binding.postal.text = intent.getStringExtra("propertyPostal")
        binding.city.text = intent.getStringExtra("propertyCity")
        binding.bed.text = intent.getIntExtra("propertyBed",0).toString()
        binding.bath.text = intent.getIntExtra("propertyBath",0).toString()
        binding.room.text = intent.getIntExtra("propertyRoom",0).toString()


        val propertyID = intent.getStringExtra("propertyID")

        binding.download.setOnClickListener {
            //ADDING THE IMAGEVIEW BACK
            binding.imageView2.setVisibility(View.VISIBLE)
            binding.photoText.setVisibility(View.VISIBLE)
            binding.imageView2.getLayoutParams().height = 426
            binding.imageView2.getLayoutParams().width = 640

            val imageName = intent.getStringExtra("propertyAddress").toString()
            val storageRef = FirebaseStorage.getInstance().reference.child("images/$imageName.jpg")

            val localfile = File.createTempFile("tempImage","jpg")
            storageRef.getFile(localfile)
                .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imageView2.setImageBitmap(bitmap)
                }

            //REMOVING THE BUTTON
            binding.download.setVisibility(View.GONE)

        }
        binding.locationFAB.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("propertyAddress", binding.address.text.toString())
            intent.putExtra("propertyCity", binding.city.text.toString())
            intent.putExtra("propertyPostal", binding.postal.text.toString())
            startActivity(intent)
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
                startActivity(Intent(applicationContext, UploadActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun transformAge() :String{
        return ""
    }
}
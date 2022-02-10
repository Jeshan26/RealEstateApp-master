package com.GC200419191.realestateapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.GC200419191.realestateapp.databinding.ActivityMainBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class MainActivity : AppCompatActivity(), PropertyAdapter.PropertyItemListener {


    // firebase Auth
    // view binding
    private lateinit var binding: ActivityMainBinding
    val imageRef = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get data from the view model(RecyclerViewAdapter)
        // This gets the data from the Firebase and then passes it to the adapter where adapter will put in child xml and then child xml will to parent xml
        val viewModel : PropertyListViewModel by viewModels()
        viewModel.getRestaurants().observe( this, {
            //passing list of properties we got from "PropertyListViewModel" as arguments using
            // lambda functions
                properties ->
            //creating instance of adapter class
            var propertyAdapter = PropertyAdapter(this, properties,this)
            binding.gridRecyclerView.adapter = propertyAdapter
        })

//      When user is going to click on Login
        binding.add.setOnClickListener{
            startActivity(Intent(this,UploadActivity::class.java))
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
                //startActivity(Intent(applicationContext, MainActivity::class.java))
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

    override fun propertySelected(property: Property) {
        // Here we are going to change the activity
        val intent = Intent(this, DescriptionActivity::class.java)
        // This how we pass the information from one interface to other(This is very case sensitive stuff so make sure it si right)
        intent.putExtra("propertyID", property.id)
        intent.putExtra("propertyAddress", property.address)
        intent.putExtra("propertyPostal", property.postalCode)
        intent.putExtra("propertyCity", property.city)
        intent.putExtra("propertyBed", property.bed)!!.toString()
        intent.putExtra("propertyBath", property.bath)
        intent.putExtra("propertyRoom", property.room)

        // Running the activity
        startActivity(intent)
    }


}
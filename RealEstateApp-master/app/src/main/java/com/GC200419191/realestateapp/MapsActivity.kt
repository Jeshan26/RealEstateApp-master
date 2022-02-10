package com.GC200419191.realestateapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.GC200419191.realestateapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var lastLocation : Location

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addressEditText.setText(intent.getStringExtra("propertyAddress")+", "+intent.getStringExtra("propertyCity")+", "
                +intent.getStringExtra("propertyPostal"))
            //intent.getStringExtra("propertyAddress")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.searchButton.setOnClickListener {
            updateLocation()
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Add a marker in Sydney and move the camera
        val location = LatLng(44.4116, -79.6683)
        mMap.addMarker(MarkerOptions().position(location).title("Marker in Sydney"))
        // setting the zoom layer
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 36f))
        // enabling  zoom in the map
        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.setOnMarkerClickListener (this)
        setUpMap()
    }

    private fun setUpMap() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        // this is getting the location of the device and using it to update the map
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if(location != null){
                //storing it locally now
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                // Removing this because we don't need to show this in app
                 placeMarkerOnMap(currentLatLng)
            }

        }
    }

    private fun placeMarkerOnMap(currentLatLng: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLng)
        mMap.addMarker(markerOptions)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
    }

    private fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng?{
        val coder = Geocoder(context)
        val address : List<Address>?
        var p1: LatLng? = null
        try{
            address = coder.getFromLocationName(strAddress, 5)
            if(address == null){return null}
            val location: Address = address[0]
            location.latitude
            location.longitude
            p1 = LatLng(location.latitude, location.longitude)
        }
        catch(e: Exception){
            e.printStackTrace()
        }
        return p1
    }

    // adding marker on the map
    private fun updateLocation() {
        var location = getLocationFromAddress(this, binding.addressEditText.text.toString())
        if(location != null){
            placeMarkerOnMap(location)
        }
        else{
            Toast.makeText(this, "Location not found", Toast.LENGTH_LONG).show()
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
       return false
    }

}
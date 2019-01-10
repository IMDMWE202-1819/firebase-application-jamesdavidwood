package com.jamesdavidwood.mwdassignment2

import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val db = FirebaseFirestore.getInstance()
    val dbref = FirebaseStorage.getInstance().reference

    private var imageToUpload:Uri? = null
    private var imageDownload:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in current location and move the camera
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener{location: Location ->
                val currentlocation = LatLng(location.latitude, location.longitude)
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(currentlocation))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 12.0f))
            }

        }
        catch (ex:SecurityException) {
            Log.w("Location Error", "Security Error", ex)

        }

    }
}
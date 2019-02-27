package com.jamesdavidwood.mwdassignment2

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.activity_upload.view.*
import kotlinx.android.synthetic.main.marker_view.*
import kotlinx.android.synthetic.main.marker_view.view.*
import java.util.*
import kotlin.math.log

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storageRef = FirebaseStorage.getInstance().reference

    private var imageToUpload: Uri? = null
    private var imageDownload: String? = null
    private val MarkerMap = hashMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Toast.makeText(this, "Hi there! For the best experience enable location services. ", Toast.LENGTH_LONG).show()
        mMap.setOnMarkerClickListener { marker ->
            val id: String = MarkerMap[marker.id] as String
            db.collection("events")
                .document(id)
                .get()
                .addOnCompleteListener { task ->
                    val document = task.result
                    if (document != null) {
                        val eventName = document.get("title") as String
                        val eventDesc = document.get("description") as String
                        val eventPhoto = document.get("image_url") as String?
                        val dialogView = layoutInflater.inflate(R.layout.marker_view, null)
                        val MarkerNameView =
                            dialogView.findViewById<TextView>(R.id.MarkerNameView)
                        MarkerNameView.text = eventName
                        val MarkerDescView =
                            dialogView.findViewById<TextView>(R.id.MarkerDescView)
                        MarkerDescView.text = eventDesc
                        val MarkerImageView =
                            dialogView.findViewById<ImageView>(R.id.MarkerImageView)
                        try {
                            if (eventPhoto != null && !eventPhoto.isEmpty()) {
                                Picasso.get().load(eventPhoto).into(MarkerImageView)
                            }
                        } catch (e: Exception) {
                            println("Caught Picasso Exception - URL could be malformed")
                            println(MarkerImageView)
                        }
                        val builder = AlertDialog.Builder(this)
                        builder.setView(dialogView).setPositiveButton("OK") { dialog, _ ->

                            dialog.dismiss()
                        }.create().show()
                    }
                }
            false
        }

        db.collection("events")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result != null) {
                        val documents = result.documents
                        for (document in documents) {
                            val location = document.get("location") as GeoPoint
                            val marker =
                                mMap.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)))
                            MarkerMap[marker.id] = document.id
                        }
                    }
                }
            }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            fusedLocationClient.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    val location = it.result

                    if (location != null) {
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                17.0f
                            )
                        )
                    }
                }
            }
        } catch (ex: SecurityException) {
            Log.w("GEO", "security error", ex)
        }

        val PICK_IMAGE = 1234

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == PICK_IMAGE) {
                if (data != null) {
                    imageToUpload = data.data
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }

        private suspend fun uploadImage() {
            GlobalScope.async {
                if (imageToUpload != null) {
                    val imageUri = imageToUpload
                    val email = mAuth.currentUser!!.email
                    val currentDate = Calendar.getInstance().time
                    val imageRef = storageRef.child("$email/$currentDate.png")
                    if (imageUri != null) {
                        val task = imageRef.putFile(imageUri)

                        while (task.isInProgress) {
                            delay(10)
                        }
                        if (task.isSuccessful) {
                            val meta = task.snapshot.metadata
                            val uploadRef = meta?.reference
                            if (uploadRef != null) {
                                val downloadUrl = uploadRef.downloadUrl
                                while (!downloadUrl.isComplete) {
                                    delay(10)
                                }
                                if (downloadUrl.isSuccessful) {
                                    imageDownload = downloadUrl.result.toString()
                                }
                            }
                        }
                    }
                }
            }.await()
        }
    }
    fun onFabClicked (view)


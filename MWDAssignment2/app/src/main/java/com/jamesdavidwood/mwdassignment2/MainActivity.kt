package com.jamesdavidwood.mwdassignment2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth



class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }
    val mAuth = FirebaseAuth.getInstance()
    //    // Choose authentication providers
    val providers = arrayListOf(

        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ( requestCode == RC_SIGN_IN) {
            if ( resultCode == RESULT_OK) {
                var user = mAuth.currentUser
                if (user != null) {
                    Log.w("USER", user.email)
                    intent= Intent(this,MapsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

// Create and launch sign-in intent

}

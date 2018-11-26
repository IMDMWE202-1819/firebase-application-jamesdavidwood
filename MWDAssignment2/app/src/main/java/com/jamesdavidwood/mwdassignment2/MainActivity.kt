package com.jamesdavidwood.mwdassignment2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import FirebaseAuth
import com.google.firebase.auth.FirebaseAuth



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    val private private mAuth = FirebaseAuth
    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build(),
        AuthUI.IdpConfig.TwitterBuilder().build())

// Create and launch sign-in intent
    startActivityForResult(
    AuthUI.getInstance()
    .createSignInIntentBuilder()
    .setAvailableProviders(providers)
    .build(),
    RC_SIGN_IN)
}

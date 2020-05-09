package com.keepnote.view.exportbackup

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.keepnote.HomeScreen
import com.keepnote.HomeScreen.Companion.GOOGLE_SIGN_IN


class RemoteBackup(activitys:HomeScreen) {
    private val TAG = "Google Drive Activity"

    var activity: HomeScreen? = null

    init {
        activity = activitys
    }


    fun connectToDrive(backup: Boolean) {
            val account = GoogleSignIn.getLastSignedInAccount(activity)
            if (account == null) {
                signIn()
            } else {
                //Initialize the drive api
                if (activity!=null){

                    if (backup) startDriveBackup() else startDriveRestore()
                }

            }
        }

    private fun startDriveRestore() {

    }

    private fun startDriveBackup() {

    }



    private fun signIn() {
        Log.d("TAG", "Start sign in")
        val googleSignInClient: GoogleSignInClient = buildGoogleSignInClient()
        activity?.startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SIGN_IN)
    }

    private fun buildGoogleSignInClient(): GoogleSignInClient {
        val signInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                .build()
        return GoogleSignIn.getClient(activity!!, signInOptions)
    }


}
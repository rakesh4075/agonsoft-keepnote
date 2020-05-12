package com.keepnote.view.exportbackup

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.keepnote.HomeScreen
import com.keepnote.HomeScreen.Companion.GOOGLE_SIGN_IN
import com.keepnote.model.preferences.StoreSharedPrefData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList


class RemoteBackup(activitys:HomeScreen,val listner:DriveUtil?) {
    private val TAG = "Google Drive Activity"
    val uiScope = CoroutineScope(Dispatchers.Default)
    var activity: HomeScreen? = null
    val midnight= false

    init {
        activity = activitys
    }



    fun connectToDrive(backup: Boolean) {
            val account = GoogleSignIn.getLastSignedInAccount(activity)
            if (account == null) {
                signIn()
            }else if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity),
                Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE_APPDATA))){
                signIn()
            }else {
                //Initialize the drive api
                if (activity!=null){
                    if (backup) {
                        StoreSharedPrefData.INSTANCE.savePrefValue("drivealert",true,activity!!)
                        startDriveBackup(account)
                    } else startDriveRestore()
                }

            }
        }

    private fun startDriveRestore() {

    }

    private fun startDriveBackup(account: GoogleSignInAccount) {
        try {
            val credential = GoogleAccountCredential.usingOAuth2(activity, mutableListOf(DriveScopes.DRIVE_FILE,DriveScopes.DRIVE_APPDATA))
            credential.selectedAccount = account.account
            val driveService= Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),credential)
                .setApplicationName("KeepNote")
                .build()
            uiScope.launch {
                val fileid = listFile(driveService)
                if (fileid!=null && fileid.isNotEmpty()){
                    if (!midnight){
                        listner?.showProgress(30)
                        val retriveFile = uiScope.async {
                            retriveFile(driveService,fileid[fileid.size-1])

                        }.await()
                        listner?.showProgress(100)
                        activity?.let { ExportBackup().restore(1, it) }
                    }else{
                        for (i in 0 until fileid.size){
                            deleteFile(driveService,fileId = fileid[i])
                            if (fileid.size==1) createFile(driveService)
                            val fileid = listFile(driveService)
                            if (fileid != null) {
                                val retriveFile = uiScope.async {
                                    retriveFile(driveService,fileid[fileid.size-1])
                                }.await()
                                activity?.let { ExportBackup().restore(1, it) }

                            }
                        }
                    }

                }else{
                    createFile(driveService)
                }
            }

        }catch (e:Exception){
            Log.d("@@@@@",e.printStackTrace().toString())
        }

        }


    private fun createFile(driveService: Drive) {
        val forder = java.io.File((activity?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()))
        val listFile: Array<java.io.File>? = forder.listFiles()
        if (listFile!=null){
            if (listFile.isNotEmpty()){
                if (!(listFile[listFile.size-1].name=="drive_db")){
                    val filePath = java.io.File(listFile[listFile.size-1].absolutePath)
                    val fileMetadata = File()
                    fileMetadata.name = filePath.name
                    fileMetadata.parents = Collections.singletonList("appDataFolder")
                    val mediaContent = FileContent("application/db", filePath)
                    val file: File = driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute()
                    Log.d("@@@@@",file.id)
                }else{
                    activity?.let { ExportBackup().backup(it) }
                    createFile(driveService)
                }

            }else{
                activity?.let { ExportBackup().backup(it) }
                createFile(driveService)
            }
        }

    }

    suspend fun retriveFile(driveService: Drive, fileid: String){
        val outputStream = ByteArrayOutputStream()
        driveService.files().get(fileid)
            .executeMediaAndDownloadTo(outputStream)

        activity?.let { createFolder(it) }
        val fileOutputStream = FileOutputStream("/storage/emulated/0/Android/data/com.keepnote/files/Documents/drive_db")

        try {
            outputStream.writeTo(fileOutputStream)
            listner?.showProgress(70)
        }catch (E:Exception){
            Log.d("@@@",E.message)
        }finally {
            fileOutputStream.close()
        }
        Log.d("@@@@output",outputStream.toString())
    }

    suspend fun listFile(driveService: Drive):ArrayList<String>?{
        var listofId = ArrayList<String>()
        val files = driveService.files().list()
            .setSpaces("appDataFolder")
            .setFields("nextPageToken, files(id, name)")
            .setPageSize(100)
            .execute()

        for(file in files.files){
            listofId.add(file.id)
        }
        return listofId
    }

    suspend fun deleteFile(driveService: Drive,fileId:String){
        try {
            driveService.files().delete(fileId).execute()
            Log.d("@@@","deleted")

        }catch (e:Exception){
            Log.d("@@@@@2",e.message)
        }
    }

    private fun signIn() {
        Log.d("TAG", "Start sign in")
        val googleSignInClient: GoogleSignInClient = buildGoogleSignInClient()
        activity?.startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SIGN_IN)
    }

    // create folder if it not exist
    private fun createFolder(context: Context) {
        val sd =
            java.io.File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString())
        if (!sd.exists()) {
            sd.mkdir()
            listner?.showProgress(45)
            Log.d("@@@@","folder created")
        } else {
            listner?.showProgress(45)
            Log.d("@@@@","folder exists")
        }

    }

    private fun buildGoogleSignInClient(): GoogleSignInClient {
        val signInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE_APPDATA))
                .build()
        return GoogleSignIn.getClient(activity!!, signInOptions)
    }
    }







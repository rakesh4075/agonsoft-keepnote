package com.keepnote.view.exportbackup

//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.api.client.extensions.android.http.AndroidHttp
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

//import com.google.api.services.drive.Drive
//import com.google.api.services.drive.DriveScopes
//import com.google.api.services.drive.model.File
import com.keepnote.view.homescreen.HomeScreen


class RemoteBackup(activitys: HomeScreen) {
    var activity: HomeScreen? = null

    init {
        activity = activitys
    }



 /*   fun connectToDrive(backup: Boolean) {
        try {
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
        }catch (e:Exception){

        }

        }*/



/*    private fun startDriveBackup(account: GoogleSignInAccount) {
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
                    val firstSync = StoreSharedPrefData.INSTANCE.getPref("drivefirstsync",false,activity) as Boolean

                    if (firstSync){
                        StoreSharedPrefData.INSTANCE.savePrefValue("drivefirstsync",false, activity!!)
                        listner?.showProgress(30)
                        val retrivefile = uiScope.async {
                            retriveFile(driveService, fileid[fileid.size - 1])
                        }.await()
                        val restoreFile= uiScope.async {
                            activity?.let { ExportBackup().restore(1, it) }
                            listner?.showProgress(100)
                        }.await()
                    }else{
                        for (i in 0 until fileid.size){
                            deleteFile(driveService,fileId = fileid[i])
                            listner?.showProgress(30)
                            if (fileid.size==1) {
                                createFile(driveService)
                                listner?.showProgress(55)
                            }
                            val fileId = listFile(driveService)
                            if (fileId != null) {
                                val retrivefile = uiScope.async {
                                    retriveFile(driveService,fileId[fileId.size-1])
                                    listner?.showProgress(80)
                                }.await()
                                val restoreFile= uiScope.async {
                                    activity?.let { ExportBackup().restore(1, it) }
                                    listner?.showProgress(100)
                                }.await()


                            }
                        }
                    }

                }else{
                    createFile(driveService)
                    val fileId = listFile(driveService)
                    if (fileId != null) {
                        val retrivefile = uiScope.async {
                            retriveFile(driveService,fileId[fileId.size-1])
                        }.await()
                        val restoreFile= uiScope.async {
                            activity?.let { ExportBackup().restore(1, it) }
                            listner?.showProgress(100)
                        }.await()


                    }
                }
            }

        }catch (e:Exception){
            Log.d("@@@@@",e.printStackTrace().toString())
        }

        }


    private fun createFile(driveService: Drive) {
        val forder = java.io.File((activity?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()))
        activity?.let { ExportBackup().backup(it) }
        val listFile: Array<java.io.File>? = forder.listFiles()
        if (listFile!=null){
            if (listFile.isNotEmpty()){
                if (listFile[listFile.size-1].name != "drive_db"){
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

    private fun retriveFile(driveService: Drive, fileid: String){
        val outputStream = ByteArrayOutputStream()
        driveService.files().get(fileid)
            .executeMediaAndDownloadTo(outputStream)

        activity?.let { createFolder(it) }
        val fileOutputStream = FileOutputStream("/storage/emulated/0/Android/data/com.keepnote/files/Documents/drive_db")

        try {
            outputStream.writeTo(fileOutputStream)
            listner?.showProgress(70)
        }catch (E:Exception){
            Log.d("@@@",E.message!!)
        }finally {
            fileOutputStream.close()
        }
        Log.d("@@@@output",outputStream.toString())
    }

    private fun listFile(driveService: Drive):ArrayList<String>?{
        val listofId = ArrayList<String>()
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

    private fun deleteFile(driveService: Drive, fileId:String){
        try {
            driveService.files().delete(fileId).execute()
            Log.d("@@@","deleted")

        }catch (e:Exception){
            Log.d("@@@@@2",e.message!!)
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
    }*/



    }







package com.keepnote.view.exportbackup

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.keepnote.HomeScreen
import com.keepnote.Html2Pdf
import com.keepnote.R
import com.keepnote.databinding.ActivityBackupBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.NoteDatabase
import com.keepnote.roomdatabasebackupandrestore.Backup
import com.keepnote.roomdatabasebackupandrestore.Restore
import com.keepnote.tedpermission.TedPermission
import com.keepnote.utils.Constants
import com.keepnote.viewmodel.HomeViewmodel
import com.keepnote.viewmodel.HomeViewmodelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExportBackup : AppCompatActivity() {

    private var listFileInfo: ArrayList<FileInfo>?=null
    private var backupDBPath: String=""
    lateinit var mbinding:ActivityBackupBinding
    private lateinit var backupAdapter:BackupListAdapter
    private lateinit var viewmodel: HomeViewmodel
    private var showtoolbarView = false
    private lateinit var mProgressDialog: ProgressDialog
    // folder on sd to backup data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if ((StoreSharedPrefData.INSTANCE.getPref("isDarktheme",false,this))as Boolean){
            setTheme(R.style.DarkTheme)
            showtoolbarView = true
        }
        else
            setTheme(R.style.LightTheme)

        mbinding = DataBindingUtil.setContentView(this,R.layout.activity_backup)
        //init viewmodel
        val application = requireNotNull(this).application
        val dataSource = NoteDatabase.invoke(this).getNoteDao()
        val homeViewmodelFactory = HomeViewmodelFactory(dataSource,application)
        viewmodel = ViewModelProviders.of(this,homeViewmodelFactory).get(HomeViewmodel::class.java)
        mbinding.viewmodel =viewmodel

        mbinding.toolbar.toolbar.title=""
        mbinding.toolbar.toolbartitle.text="Exports & Backup"
        if (showtoolbarView)  mbinding.toolbar.vw1.visibility = View.GONE
        setSupportActionBar(mbinding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        mbinding.errLayout.errmsg.text="Your backup files will be displayed here"
        mProgressDialog = Constants.setupProgressDialog(this)
        populateDB()



        mbinding.exportbackup.setOnClickListener {
            backup(this) }


//        mbinding.exporttxt.setOnClickListener {
//            restore(0,this)}

        mbinding.exporttxt.setOnClickListener {
            if (TedPermission.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                mProgressDialog.show()
                try {
                    converthtmlTopdf()
                }catch (e:Exception){
                }

            }else Constants.verifyPermission(this)
        }

        mbinding.delete.setOnClickListener {
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
            builder.setTitle("Delete").setIcon(null)
                .setMessage("Are you sure to delete all backups forever?")
            builder.setPositiveButton(R.string.no) { dialog, which ->
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.yes) { dialog, which ->
                deleteAllFile()
            }
            builder.show()
        }
    }

    private fun deleteAllFile() {
        val forder = File((getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()))
        val listFile: Array<File>? = forder.listFiles()
        listFileInfo = listFile?.size?.let { ArrayList(it) }
        if (listFile!=null){
            for (file in listFile){
                val files = File(file.absolutePath)
                if (files.exists()) files.delete()
                populateDB()
            }
        }

    }

    private fun populateDB() {
        listFileInfo = ArrayList()
        val forder = File((getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()))
        val listFile: Array<File>? = forder.listFiles()
        listFileInfo = listFile?.size?.let { ArrayList(it) }
        if (listFile!=null){
            for (file in listFile){
                val fileName = file.name
                val fileModifedDate = Date(file.lastModified()).toString()
                val fileSize = Integer.parseInt(((file.length()/1024).toString())).toString()
                if (fileName!="drive_db")
                    listFileInfo?.add(FileInfo(fileName,fileModifedDate,fileSize))
            }
        }
        val layoutManager = LinearLayoutManager(this)
        mbinding.backuprecycler.layoutManager = layoutManager

        if (listFileInfo?.isEmpty()!!){
            mbinding.errLayout.root.visibility = View.VISIBLE
            backupAdapter =BackupListAdapter(listFileInfo!!)
            mbinding.backuprecycler.adapter = backupAdapter
            backupAdapter.notifyDataSetChanged()
        }else{
            mbinding.errLayout.root.visibility = View.GONE
            backupAdapter =BackupListAdapter(listFileInfo!!)
            mbinding.backuprecycler.adapter = backupAdapter
            backupAdapter.notifyDataSetChanged()
        }

    }

    fun restore(restoreFor:Int,activity: Activity) {
        if (TedPermission.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            var backupFilePath = ""
            if (restoreFor == 0) {
                backupFilePath =
                    "/storage/emulated/0/Android/data/com.keepnote/files/Documents/note_2020_05_01__15_32_31"
            } else backupFilePath =
                "/storage/emulated/0/Android/data/com.keepnote/files/Documents/drive_db"

            Restore.Init()
                .database(NoteDatabase.invoke(this))
//            .backupFilePath("/storage/emulated/0/Android/data/com.keepnote/files/Documents/file.db")
                .backupFilePath(backupFilePath)
                .secretKey("123")
                .onWorkFinishListener { _, message ->
                    Constants.showToast(message, activity)
                    if (restoreFor == 0) {
                        startActivity(Intent(activity, HomeScreen::class.java))
                        finish()
                    } else {
                        (activity as HomeScreen).initFragment(1)


                    }

                }
                .execute()
        }else{
            Constants.verifyPermission(this)
        }

    }

    fun backup(context: Context) {
        if (TedPermission.isGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            createFolder(context)
            val formatTime = SimpleDateFormat("yyyy_MM_dd__HH_mm_ss", Locale("en"))
            backupDBPath = "note" + "_" + formatTime.format(Date())
            Backup.Init()
                .database(NoteDatabase.invoke(context))
                .path("/storage/emulated/0/Android/data/com.keepnote/files/Documents")
                .fileName(backupDBPath)
                .secretKey("123")
                .onWorkFinishListener { success, message ->
                    if (success){
                        populateDB()
                        Constants.showToast("Backed up successfully", context)
                    }else
                        Constants.showToast(message, context)
                }
                .execute()
        }else{
            Constants.verifyPermission(this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
        return true
    }



    private fun converthtmlTopdf() {
        var sd:File?=null
        val folderSD: String? = createFolder(this).absolutePath
        folderSD?.let {
            sd = File(folderSD,"pdfDoc")
        }
        if (!sd!!.exists()) {
            sd?.mkdir()
            createPdfFile(sd)
        } else {
            createPdfFile(sd)
        }





    }


    fun  createPdfFile(sd: File?) = try {
        if (Constants.isInternetAvailable(this)){
            var file:File?=null
            var html = ""
            viewmodel.getallNotes()
            viewmodel.allNotes.observe(this, androidx.lifecycle.Observer {notes->
//                val html = "<body>\n" +
//                        "\n" +
//                        "<h2>Using a Full URL File Path</h2>\n" +
//                        "<img src=\"https://www.w3schools.com/images/picture.jpg\" alt=\"Mountain\" style=\"width:300px\">\n" +
//                        "\n" +
//                        "</body>"
                for (i in notes.indices){
                    html = html +notes[i].title+ "<br>"+ notes[i].content + "\n"+ "<br>"+ "<br>"
                }
                file =File(sd,"keepnote.pdf")
                val converter = Html2Pdf.Companion.Builder()
                    .context(this)
                    .html(html)
                    .file(file!!)
                    .build()


                converter.convertToPdf(object: Html2Pdf.OnCompleteConversion {
                    override fun onSuccess(msg: String) {
                        try {

                            if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                            Constants.showToast(msg,this@ExportBackup)
                            val uri:Uri? = try {
                                FileProvider.getUriForFile(this@ExportBackup, applicationContext.packageName + ".provider",
                                    file!!)
                            } catch (e:Exception){
                                null
                            }

                            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(uri,"application/pdf")
                                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                startActivity(intent)
                            }else{
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(Uri.parse(file?.absolutePath),"application/pdf")
                                val intentfinal = Intent.createChooser(intent,"Open File")
                                intentfinal.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intentfinal)
                            }

                        }catch (e:Exception){
                        }



                    }

                    override fun onFailed(msg: String) {
                        if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                        mbinding.progressBar.visibility = View.GONE
                        Constants.showToast(msg,this@ExportBackup)
                    }
                })
            })
        }else{
            if (mProgressDialog.isShowing) mProgressDialog.dismiss()
            Constants.showToast("Check network connection",this)
        }
    }catch (e:Exception){

    }

    // create folder if it not exist
    fun createFolder(context: Context):File {
        val sd = File(context.getExternalFilesDir(null).toString())
        if (!sd.exists()) {
            sd.mkdir()
        } else {
        }
        return sd
    }

}

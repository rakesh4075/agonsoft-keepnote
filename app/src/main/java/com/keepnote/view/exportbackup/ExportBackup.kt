package com.keepnote.view.exportbackup

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.keepnote.HomeScreen
import com.keepnote.R
import com.keepnote.databinding.ActivityBackupBinding
import com.keepnote.roomdatabasebackupandrestore.Backup
import com.keepnote.roomdatabasebackupandrestore.Restore
import com.keepnote.tedpermission.TedPermission
import com.keepnote.utils.Constants
import com.raks.roomdatabase.NoteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class ExportBackup : AppCompatActivity() {

    private var listFileInfo: ArrayList<FileInfo>?=null
    private var backupDBPath: String=""
    lateinit var mbinding:ActivityBackupBinding
    private lateinit var backupAdapter:BackupListAdapter
    // folder on sd to backup data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       mbinding = DataBindingUtil.setContentView(this,R.layout.activity_backup)

        mbinding.toolbar.toolbar.title=""
        mbinding.toolbar.toolbartitle.text="Exports & Backup"
        mbinding.toolbar.toolbarSearch.visibility = View.VISIBLE
        setSupportActionBar(mbinding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mbinding.errLayout.errmsg.text="Your backup files will be displayed here"

        populateDB()



        mbinding.exportbackup.setOnClickListener {
            backup(this) }


        mbinding.exporttxt.setOnClickListener {
            restore(0,this)}

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
         var backupFilePath = ""
         if (restoreFor==0){
             backupFilePath = "/storage/emulated/0/Android/data/com.keepnote/files/Documents/note_2020_05_01__15_32_31"
         } else backupFilePath = "/storage/emulated/0/Android/data/com.keepnote/files/Documents/drive_db"

        Restore.Init()
            .database(NoteDatabase.invoke(this))
//            .backupFilePath("/storage/emulated/0/Android/data/com.keepnote/files/Documents/file.db")
            .backupFilePath(backupFilePath)
            .secretKey("123")
            .onWorkFinishListener {
                    success, message -> Constants.showToast("$message",activity)
                if (restoreFor==0){
                    startActivity(Intent(activity,HomeScreen::class.java))
                    finish()
                }else{

                        Log.d("@@@@","frgament")
                        (activity as HomeScreen).initFragment(1)


                }

            }
            .execute()

    }

     fun backup(context: Context) {
        if (TedPermission.isGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            createFolder(context)
            val formatTime = SimpleDateFormat("yyyy_MM_dd__HH_mm_ss")
            backupDBPath = "note" + "_" + formatTime.format(Date())
            Log.d("@@@@@dbpath",backupDBPath)
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
                    Constants.showToast("$message", context)
                }
                .execute()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
        return true
    }





    // create folder if it not exist
    private fun createFolder(context: Context) {
            val sd = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString())
            if (!sd.exists()) {
                sd.mkdir()
                Log.d("@@@@","folder created")
            } else {
                Log.d("@@@@","folder exists")
            }

    }

}

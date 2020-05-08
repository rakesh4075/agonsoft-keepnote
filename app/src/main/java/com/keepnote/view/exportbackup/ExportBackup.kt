package com.keepnote.view.exportbackup

import android.Manifest
import android.app.AlertDialog
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
            backup() }


        mbinding.exporttxt.setOnClickListener {
            restore()}

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

    private fun restore() {
        Restore.Init()
            .database(NoteDatabase.invoke(this))
//            .backupFilePath("/storage/emulated/0/Android/data/com.keepnote/files/Documents/file.db")
            .backupFilePath("/storage/emulated/0/Android/data/com.keepnote/files/Documents/note_2020_05_01__15_32_31")
            .secretKey("123")
            .onWorkFinishListener {
                    success, message -> Constants.showToast("$message",this@ExportBackup)
                startActivity(Intent(this,HomeScreen::class.java))
                finish()
            }
            .execute()

    }

    private fun backup() {
        if (TedPermission.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            createFolder()
            val formatTime = SimpleDateFormat("yyyy_MM_dd__HH_mm_ss")
            backupDBPath = "note" + "_" + formatTime.format(Date())
            Log.d("@@@@@dbpath",backupDBPath)
            Backup.Init()
                .database(NoteDatabase.invoke(this))
                .path("/storage/emulated/0/Android/data/com.keepnote/files/Documents")
                .fileName(backupDBPath)
                .secretKey("123")
                .onWorkFinishListener { success, message ->
                    if (success){
                        populateDB()
                        Constants.showToast("Backed up successfully", this@ExportBackup)
                    }else
                    Constants.showToast("$message", this@ExportBackup)
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
    private fun createFolder() {
            val sd = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString())
            if (!sd.exists()) {
                sd.mkdir()
                Log.d("@@@@","folder created")
            } else {
                Log.d("@@@@","folder exists")
            }

    }

}

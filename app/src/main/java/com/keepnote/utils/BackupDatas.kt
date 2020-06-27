package com.keepnote.utils

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import com.keepnote.HomeScreen
import com.keepnote.R
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BackupDatas(private val context: Context) {

    // url for database
    private val dataPath =
        "//data//com.keepnote.keepnote//databases//"

    // name of main data
    private val databaseName: String = "note_db"

    // data main
    private val data = dataPath + databaseName

    // name of temp data
    private val dataTempName: String = databaseName + "_temp"

    // temp data for copy data from sd then copy data temp into main data
    private val dataTemp = dataPath + dataTempName

    // folder on sd to backup data
    private val folderSD = context.getExternalFilesDir("").toString()+"/KeepNote"

    private var onBackupListener: OnBackupListener? = null

    // create folder if it not exist
    private fun createFolder() {
        val sd = File(folderSD)
        if (!sd.exists()) {
            sd.mkdir()
        }
    }

    /**
     * Copy database to sd card
     * name of file = database name + time when copy
     * When finish, we call onFinishExport method to send notify for activity
     */

    private fun exportToSD(){
        var error: String? = null
        try {
            createFolder()
            val sd = File(folderSD)
            if (sd.canWrite()){
                val formatTime = SimpleDateFormat("yyyy_MM_dd__HH_mm_ss", Locale.US)
                val backupDBPath: String =
                    databaseName + "_" + formatTime.format(Date())
                val currentDB =
                    File(Environment.getDataDirectory(), data)
                val backupDB = File(sd, backupDBPath)

                if (currentDB.exists()){
                    val src =
                        FileInputStream(currentDB).channel
                    val dst =
                        FileOutputStream(backupDB).channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                } else {
                    println("db not exist")
                }
            }

        }catch (e:Exception) {
            error = "Error backup"
        }
    }

    fun importFromSD(){
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
       // builder.setTitle(R.string.backup_data).setIcon(R.mipmap.ic_launcher)
            .setMessage(R.string.backup_before_import)
        builder.setPositiveButton(R.string.no) { dialog, which ->
            showDialogListFile(folderSD)
        }
        builder.setNegativeButton(R.string.yes,
            DialogInterface.OnClickListener { dialog, which ->
                showDialogListFile(folderSD)
                exportToSD()
            })
        builder.show()
    }

    private fun showDialogListFile(forderPath: String) {
        createFolder()
        val forder = File(forderPath)
        val listFile = forder.listFiles()
        val listFileName = ArrayList<String>(listFile.size)

        for (element in listFile){
            listFileName.add(element.name)
        }
        if (listFileName.size > 0) {
            // get layout for list
            val inflater: LayoutInflater = (context as HomeScreen).layoutInflater
            val convertView =
                inflater.inflate(R.layout.list_backup_file, null) as View

            val builder: AlertDialog.Builder =
                AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)

            // set view for dialog

            // set view for dialog
            builder.setView(convertView)
          //  builder.setTitle("Select file").setIcon(R.mipmap.ic_launcher)
            val alert = builder.create()
            val lv =
                convertView.findViewById<View>(R.id.lv_backup) as ListView
            val adapter = ArrayAdapter(
                context,
                android.R.layout.simple_list_item_1, listFileName
            )
            lv.adapter = adapter
            lv.onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    alert.dismiss()
                    importData(listFileName[position])
                }
            alert.show()
        } else {
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
           // builder.setTitle("Delete").setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.backup_empty)
            builder.show()
        }

    }


    private fun importData(fileNameOnSD: String) {
        val sd = File(folderSD)
        // create temp database

        // create temp database
        val dbBackup = context.openOrCreateDatabase(dataTempName,Context.MODE_ENABLE_WRITE_AHEAD_LOGGING,null)
        var error: String? = null
        if (sd.canWrite()){
            val currentDB =
                File(Environment.getDataDirectory(), dataTemp)
            val backupDB = File(sd, fileNameOnSD)
            if (currentDB.exists()){
                val src: FileChannel
                try {
                    src = FileInputStream(backupDB).channel
                    val dst = FileOutputStream(currentDB)
                        .channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    error = "Error load file"
                } catch (e: IOException) {
                    error = "Error import"
                }
            }


            /**
             * when copy old database into temp database success
             * we copy all row of table into main database
             */
            if (error == null) {
                CopyDataAsyncTask(dbBackup,context).execute()
            } else {
                onBackupListener?.onFinishImport(error)
            }
        }
    }

     class CopyDataAsyncTask(private var db: SQLiteDatabase, val context: Context) : AsyncTask<Void?, Void?, Void?>() {
        private var progress = ProgressDialog(context)

        /**
         * will call first
         */
        override fun onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute()
            progress.setMessage("Importing...")
            progress.show()
        }


        /**
         * end process
         */
        override fun onPostExecute(error: Void?) {
            // TODO Auto-generated method stub
            super.onPostExecute(error)
            if (progress.isShowing) {
                progress.dismiss()
            }

        }

         override fun doInBackground(vararg params: Void?): Void? {
             copyData(db)
             return null
         }

         private fun copyData(db: SQLiteDatabase) {
             /** copy all row of subject table */
             /** copy all row of subject table  */
             val cursor: Cursor = db.query(
                 true,"notes",null,
                 null,
                 null,
                 null,
                 null,
                 null,
                 null
             )
             cursor.moveToFirst()
             while (!cursor.isAfterLast){

             }
             cursor.close()
         }


     }

    fun setOnBackupListener(onBackupListener: OnBackupListener) {
        this.onBackupListener = onBackupListener
    }

    interface OnBackupListener {
        fun onFinishExport(error: String?)
        fun onFinishImport(error: String?)
    }

}
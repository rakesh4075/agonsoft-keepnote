package com.keepnote.view.exportbackup

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.keepnote.view.homescreen.HomeScreen
import com.keepnote.R
import com.keepnote.databinding.BackupViewBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.NoteDatabase
import com.keepnote.roomdatabasebackupandrestore.Restore
import com.keepnote.utils.Constants
import com.keepnote.utils.ExceptionTrack

class BackupListAdapter(private val backupList: ArrayList<FileInfo>):RecyclerView.Adapter<BackupListAdapter.ViewHolder>() {
    class ViewHolder(binding: BackupViewBinding):RecyclerView.ViewHolder(binding.root) {
        val fileName = binding.fileName
        val fileDate = binding.lastmodifieddate
        val fileSize = binding.filesizeTxt
        val vw1 = binding.vw1
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val backupViewLayoutBinding: BackupViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.backup_view,parent,false)
        return ViewHolder(backupViewLayoutBinding)
    }

    override fun getItemCount(): Int {
        return backupList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lastDateTime = backupList[position].fileLastModifiedDate.split(" ")

        holder.fileName.text = backupList[position].fileName
        holder.fileDate.text = "${lastDateTime[2]} ${lastDateTime[1]} ${lastDateTime[3].split(":")[0]}:${lastDateTime[3].split(":")[1]}"
        holder.fileSize.text = backupList[position].fileSize+" kb"

        if ((StoreSharedPrefData.INSTANCE.getPref("isDarktheme",false,holder.itemView.context))as Boolean) holder.vw1.visibility = View.GONE
        if (position==itemCount-1) holder.vw1.visibility = View.GONE
        holder.itemView.setOnClickListener {
            try {
                Restore.Init()
                    .database(NoteDatabase.invoke(holder.itemView.context))
                    .backupFilePath("/storage/emulated/0/Android/data/com.keepnote/files/Documents/${backupList[position].fileName}")
                    .secretKey("123")
                    .onWorkFinishListener { success: Boolean, s: String ->
                        try {
                            if (success){
                                Constants.showToast("Restored",holder.itemView.context)
                                val intent = Intent(holder.itemView.context,
                                    HomeScreen::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                holder.itemView.context.startActivity(intent)
                            }else
                                Constants.showToast(s,holder.itemView.context)

                        }catch (e:Exception){
                            ExceptionTrack.getInstance().TrackLog(e)
                        }

                    }
                    .execute()

            }catch (e:java.lang.Exception){
                Log.d("@@@@",e.message.toString())
            }


        }
    }


}
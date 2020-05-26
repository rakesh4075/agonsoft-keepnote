package com.keepnote.view.settings

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.keepnote.NoteListAdapter
import com.keepnote.R
import com.keepnote.databinding.SettingsItemBinding
import com.keepnote.model.preferences.StoreSharedPrefData

class SettingsRecyclerAdapter(val listerner:NoteListAdapter.NotesListner) :RecyclerView.Adapter<SettingsRecyclerAdapter.ViewHolder>() {
    private var titleList = arrayOf("Revert","Font size","Notes sort order","Online Sync","Backup","Night mode")
    private var settingsIcon = arrayOf(R.drawable.ic_undo,R.drawable.ic_format_size,R.drawable.ic_filter,R.drawable.ic_sync_black,R.drawable.ic_backup,R.drawable.ic_darkness)
    private var contentList = arrayOf("Revert to default settings","Set the default notes font size","Set the default notes sort order","Sync on launch","Auto backup","Set dark or light background")
    class ViewHolder(binding: SettingsItemBinding):RecyclerView.ViewHolder(binding.root) {
        val title = binding.title
        val settingImage= binding.settingsIcon
        val settingValue = binding.settingValue
        val content = binding.content
        val toggle = binding.settingToogle


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val settingViewLayoutBinding: SettingsItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.settings_item,parent,false)
        return ViewHolder(settingViewLayoutBinding)
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.title.text = titleList[position]
        holder.content.text = contentList[position]
        holder.settingImage.setBackgroundResource(settingsIcon[position])
        if ((StoreSharedPrefData.INSTANCE.getPref("isDarktheme",false,context))as Boolean){
            if (position==itemCount-1)
            holder.toggle.isChecked = true
        }
        if (position>=3 || position==0){
            if (position==0)holder.toggle.visibility = View.GONE
            holder.settingValue.visibility=View.GONE
        }else{
            holder.toggle.visibility = View.GONE
        }

        holder.toggle.setOnCheckedChangeListener { buttonView, isChecked ->

            when(position){
                itemCount-1->{
                    if (isChecked){
                        StoreSharedPrefData.INSTANCE.savePrefValue("isDarktheme",true,context)
                        if (context!=null){
                            if (context is Settings){
                                (context as Activity).finish()
                                context.startActivity(Intent(context,Settings::class.java))
                            }
                        }
                    }else{
                        StoreSharedPrefData.INSTANCE.savePrefValue("isDarktheme",false,context)
                        if (context!=null){
                            if (context is Settings){
                                (context as Activity).finish()
                                context.startActivity(Intent(context,Settings::class.java))
                            }
                        }
                    }
                }
                itemCount-3->{
                    StoreSharedPrefData.INSTANCE.savePrefValue("synconlaunch",true,context)
                }
            }


        }
    }
}
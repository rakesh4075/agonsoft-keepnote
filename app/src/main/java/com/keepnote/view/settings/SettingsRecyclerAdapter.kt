package com.keepnote.view.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.keepnote.R
import com.keepnote.databinding.SettingsItemBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.utils.Constants


class SettingsRecyclerAdapter :RecyclerView.Adapter<SettingsRecyclerAdapter.ViewHolder>() {
    private var titleList = arrayOf("Revert","Font size","Notes sort order","Online Sync","Backup","Night mode")
    private var settingsIcon = arrayOf(R.drawable.ic_undo,R.drawable.ic_format_size,R.drawable.ic_filter,R.drawable.ic_sync_black,R.drawable.ic_backup,R.drawable.ic_darkness)
    private var contentList = arrayOf("Revert to default settings","Set the default notes font size","Set the default notes sort order","Sync on launch","Auto backup","Set dark or light background")


    class ViewHolder(binding: SettingsItemBinding):RecyclerView.ViewHolder(binding.root) {
        private  var layout:ConstraintLayout = binding.rootview
        private val params = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        val title = binding.title
        val settingImage= binding.settingsIcon
        val settingValue = binding.settingValue
        val content = binding.content
        val toggle = binding.settingToogle
        val view = binding.rootview

        fun hideLayout(){
            params.height=0
            layout.layoutParams = params
        }
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
        holder.settingImage.background = AppCompatResources.getDrawable(context,settingsIcon[position])

        if (position==1){
            holder.settingValue.visibility = View.VISIBLE
            holder.settingValue.text = getSettingsValue(holder.itemView.context,1)
        }
        else if (position==2){
            holder.settingValue.visibility = View.VISIBLE
            holder.settingValue.text = getSettingsValue(holder.itemView.context,2)
        }

        if ((StoreSharedPrefData.INSTANCE.getPref("isDarktheme",false,context))as Boolean){
            if (position==itemCount-1)
            holder.toggle.isChecked = true
        }
        if ((StoreSharedPrefData.INSTANCE.getPref("synconlaunch",false,context))as Boolean){
            if (position==itemCount-3)
                holder.toggle.isChecked = true
        }
        if ((StoreSharedPrefData.INSTANCE.getPref("autobackup",false,context))as Boolean){
            if (position==itemCount-2)
                holder.toggle.isChecked = true
        }
        if (position>=3 || position==0){
            if (position==0)holder.toggle.visibility = View.GONE
            holder.settingValue.visibility=View.GONE
        }else{
            holder.toggle.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            when(position){

                0->{
                    StoreSharedPrefData.INSTANCE.removePref("isDarktheme",context)
                    StoreSharedPrefData.INSTANCE.removePref("synconlaunch",context)
                    StoreSharedPrefData.INSTANCE.removePref("autobackup",context)
                    StoreSharedPrefData.INSTANCE.removePref("notefontsize",context)
                    StoreSharedPrefData.INSTANCE.removePref("notesortorder",context)
                    if (context!=null){
                        if (context is Settings){
                            (context as Activity).finish()
                            context.startActivity(Intent(context,Settings::class.java))
                            Constants.showToast("Reverted!",context)
                        }
                    }

                }
                1->{
                    createPopupmenu(holder,1)

                }
                2->{
                    createPopupmenu(holder,2)

                }
            }

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
                    if (isChecked){
                        StoreSharedPrefData.INSTANCE.savePrefValue("synconlaunch",true,context)
                    }else
                        StoreSharedPrefData.INSTANCE.savePrefValue("synconlaunch",false,context)

                }
                itemCount-2->{
                    if (isChecked){
                        StoreSharedPrefData.INSTANCE.savePrefValue("autobackup",true,context)
                    }else
                        StoreSharedPrefData.INSTANCE.savePrefValue("autobackup",false,context)

                }
            }


        }
        if (position==3) {
            holder.hideLayout()
        }

    }

    private fun getSettingsValue(context: Context,For: Int): String {
        val value =  if (For==1) StoreSharedPrefData.INSTANCE.getPref("notefontsize",1,context) else StoreSharedPrefData.INSTANCE.getPref("notesortorder",1,context)
        if (For==1){
            when(value){
                1-> return "Default"
                2-> return "Medium"
                3-> return "Large"
            }
        }else{
            when(value){
                1-> return "Alphabetical"
                2-> return "Color"
                3-> return "Created time"
                4-> return "Modified time"
            }
        }
        return ""
    }

    private fun createPopupmenu(holder: ViewHolder,For:Int) {
        val mcontext = holder.itemView.context
        val popupMenu = PopupMenu(holder.itemView.context,holder.itemView)
        popupMenu.gravity = Gravity.END


        when(For){
            1->{
                popupMenu.menu.add("Default").setOnMenuItemClickListener {
                    StoreSharedPrefData.INSTANCE.savePrefValue("notefontsize",1,mcontext)
                    if (holder.adapterPosition==1) holder.settingValue.text="Default"
                    true
                }
                popupMenu.menu.add("Medium").setOnMenuItemClickListener {
                    StoreSharedPrefData.INSTANCE.savePrefValue("notefontsize",2,mcontext)
                    if (holder.adapterPosition==1) holder.settingValue.text="Medium"
                    true
                }
                popupMenu.menu.add("Large").setOnMenuItemClickListener {
                    StoreSharedPrefData.INSTANCE.savePrefValue("notefontsize",3,mcontext)
                    if (holder.adapterPosition==1) holder.settingValue.text="Large"
                    true
                }


            }
            2->{
                popupMenu.menu.add("Alphabetical").setOnMenuItemClickListener {
                    StoreSharedPrefData.INSTANCE.savePrefValue("notesortorder",1,mcontext)
                   if (holder.adapterPosition==2) holder.settingValue.text="Alphabetical"
                    true
                }
//                popupMenu.menu.add("Color").setOnMenuItemClickListener {
//                    StoreSharedPrefData.INSTANCE.savePrefValue("notesortorder",2,mcontext)
//                    if (holder.adapterPosition==2) holder.settingValue.text="Color"
//                    true
//                }
                popupMenu.menu.add("Created time").setOnMenuItemClickListener {
                    StoreSharedPrefData.INSTANCE.savePrefValue("notesortorder",3,mcontext)
                    if (holder.adapterPosition==2) holder.settingValue.text="Created time"
                    true
                }
                popupMenu.menu.add("Modified time").setOnMenuItemClickListener {
                    StoreSharedPrefData.INSTANCE.savePrefValue("notesortorder",4,mcontext)
                    if (holder.adapterPosition==2) holder.settingValue.text="Modified time"
                    true
                }
            }
        }

        popupMenu.show()





    }




}
package com.keepnote

import android.content.Intent
import android.graphics.BlurMaskFilter
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.keepnote.databinding.NoteViewLayoutBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.Notes
import com.keepnote.utils.Constants
import com.keepnote.view.noteview.NoteWebview
import com.keepnote.view.settings.Privacy


class NoteListAdapter(var noteList: List<Notes>,listner: NotesListner):RecyclerView.Adapter<NoteListAdapter.ViewHolder>() {

    private var notesListner: NotesListner?=null


    init {
        notesListner = listner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val noteViewLayoutBinding:NoteViewLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.note_view_layout,parent,false)
        return ViewHolder(noteViewLayoutBinding)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!(noteList[position].isDeleted==1)){
            noteList[position].title?.let {title->
                if (title.isEmpty()){
                    holder.title.text=holder.itemView.context.getString(R.string.title_empty_text)
                } else if (title.length>=20) holder.title.text = "${title.subSequence(0,20)}..."
                else holder.title.text = title
            }
            holder.content.text = Html.fromHtml(noteList[position].content)
            if (noteList[position].isFavourite==1)
            holder.favItem.visibility = View.VISIBLE
            holder.favItem.background = ContextCompat.getDrawable(holder.itemView.context,R.drawable.ic_favorite_checked)

            if (noteList[position].islocked==1)
                blurText(position,holder.content)

            val colorCode = Constants.getRandomColor()
            holder.noteCard.setCardBackgroundColor(ContextCompat.getColor(holder.view.context,colorCode))
            holder.view.setOnClickListener {
                if (noteList[position].islocked==1) return@setOnClickListener
                val intent = Intent(holder.itemView.context,NoteWebview::class.java)
                intent.putExtra("title",noteList[position].title)
                intent.putExtra("content",noteList[position].content)
                intent.putExtra("noteid",noteList[position].noteId)
                intent.putExtra("colorcode",noteList[position].notecolor)
                intent.putExtra("from",1)
                holder.itemView.context.startActivity(intent)

            }

            holder.viewmenu.setOnClickListener {
                val popupMenu = PopupMenu(holder.itemView.context,it)
                popupMenu.gravity = Gravity.END

                if (noteList[position].islocked==1){
                    popupMenu.menu.add("UnLock").setOnMenuItemClickListener {
                        val lockintent = Intent(holder.view.context,Privacy::class.java)
                        lockintent.putExtra("noteid",noteList[position].noteId)
                        lockintent.putExtra("from","home-unlock")
                        holder.itemView.context.startActivity(lockintent)
                      //  notesListner?.takeActionForNotes("updatelockbyid",position = position,noteId = noteList[position].noteId)
                        true
                    }
                }else{
                    popupMenu.menu.add("Lock").setOnMenuItemClickListener {
                        val lockpattern:String? = StoreSharedPrefData.INSTANCE.getPref("lockpattern",0,holder.view.context).toString()
                        if (lockpattern=="1"){
                            notesListner?.takeActionForNotes("updatelockbyid",position = position,noteId = noteList[position].noteId)
                        }else{
                            val lockintent = Intent(holder.view.context,Privacy::class.java)
                            lockintent.putExtra("noteid",noteList[position].noteId)
                            holder.itemView.context.startActivity(lockintent)
                            notesListner?.takeActionForNotes("updatelockbyid",position = position,noteId = noteList[position].noteId)
                        }

                        true
                    }
                }

                popupMenu.menu.add("Delete").setOnMenuItemClickListener {
                    notesListner?.takeActionForNotes("deletenotebyid",position = position,noteId = noteList[position].noteId)
                    true
                }
                popupMenu.show()

            }
        }

    }

    private fun blurText(position: Int, content: TextView) {
        content.setLayerType(View.LAYER_TYPE_SOFTWARE,null)
        val radius = content.textSize/3
        val filter = BlurMaskFilter(radius,BlurMaskFilter.Blur.NORMAL)
        content.paint.maskFilter = filter
    }

    class ViewHolder(itembinding: NoteViewLayoutBinding):RecyclerView.ViewHolder(itembinding.root) {
        val title = itembinding.titles
        val content = itembinding.content
        val view = itembinding.content
        val noteCard = itembinding.noteCard
        val viewmenu = itembinding.menuIcon
        val favItem = itembinding.favToogle
    }

    interface NotesListner{
        fun takeActionForNotes(actionFor:String,noteId: Long,position: Int)
    }
}
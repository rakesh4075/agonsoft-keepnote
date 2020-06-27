package com.keepnote.view.trash

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.keepnote.NoteListAdapter
import com.keepnote.R
import com.keepnote.databinding.NoteViewLayoutBinding
import com.keepnote.notesDB.Notes
import com.keepnote.utils.Constants
import com.keepnote.view.noteview.NoteWebview
import com.keepnote.view.settings.Privacy

class TrashAdapter(private val noteList:ArrayList<Notes>, listner: NoteListAdapter.NotesListner):RecyclerView.Adapter<TrashAdapter.ViewHolder>() {
    private var notesListner: NoteListAdapter.NotesListner?=null


    init {
        notesListner = listner
    }
    class ViewHolder(itembinding: NoteViewLayoutBinding):RecyclerView.ViewHolder(itembinding.root) {
        val title = itembinding.titles
        val content = itembinding.content
        val view = itembinding.content
        val noteCard = itembinding.noteCard
        val viewmenu = itembinding.menuIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val noteViewLayoutBinding: NoteViewLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.note_view_layout,parent,false)
        return ViewHolder(noteViewLayoutBinding)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        noteList[position].title?.let {title->
            when {
                title.isEmpty() -> {
                    holder.title.text="<Untitled>"
                }
                title.length>=20 -> holder.title.text = "${title.subSequence(0,20)}..."
                else -> holder.title.text = title
            }
        }
        if (holder.itemView.context is Privacy){
            holder.viewmenu.visibility = View.GONE
            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, NoteWebview::class.java)
                intent.putExtra("title",noteList[position].title)
                intent.putExtra("content",noteList[position].content)
                intent.putExtra("noteid",noteList[position].noteId)
                intent.putExtra("colorcode",noteList[position].notecolor)
                intent.putExtra("from",1)
                holder.itemView.context.startActivity(intent)
            }
        }
        val content = noteList[position].content.replace("<br>","",true)
        holder.content.text = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT)
        val colorCode = Constants.getRandomColor()
        holder.noteCard.setCardBackgroundColor(ContextCompat.getColor(holder.view.context,colorCode))

        holder.viewmenu.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context,it)
            popupMenu.gravity = Gravity.END
            popupMenu.menu.add("Restore").setOnMenuItemClickListener {
                notesListner?.takeActionForNotes("restorenote",noteList[position].noteId,position)
                true
            }
            popupMenu.show()
        }
    }
}
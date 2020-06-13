package com.keepnote.view.favourite

import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.keepnote.NoteListAdapter
import com.keepnote.R
import com.keepnote.databinding.NoteViewLayoutBinding
import com.keepnote.notesDB.Notes
import com.keepnote.utils.Constants

class FavouriteAdapter(val noteList:ArrayList<Notes>, listner: NoteListAdapter.NotesListner):RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {
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
            if (title.isEmpty()){
                holder.title.text="<Untitled>"
            } else if (title.length>=20) holder.title.text = "${title.subSequence(0,20)}..."
            else holder.title.text = title
        }
        holder.content.text = Html.fromHtml(noteList[position].content)
        val colorCode = Constants.getRandomColor()
        holder.noteCard.setCardBackgroundColor(ContextCompat.getColor(holder.view.context,colorCode))

    }
}
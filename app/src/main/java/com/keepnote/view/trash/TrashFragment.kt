package com.keepnote.view.trash

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.keepnote.NoteListAdapter

import com.keepnote.R
import com.keepnote.databinding.FragmentTrashBinding
import com.keepnote.notesDB.Notes
import com.keepnote.utils.Constants
import com.keepnote.viewmodel.HomeViewmodel
import com.keepnote.viewmodel.HomeViewmodelFactory
import com.raks.roomdatabase.NoteDatabase

/**
 * A simple [Fragment] subclass.
 */
class TrashFragment : Fragment(),NoteListAdapter.NotesListner {
    private  var deletedNotes: ArrayList<Notes>?=null
    private var notesize: Int = 0
    private  var noteDBAdapter: TrashAdapter?=null
    private lateinit var mbinding:FragmentTrashBinding
    lateinit var viewmodel: HomeViewmodel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mbinding = DataBindingUtil.inflate(inflater,R.layout.fragment_trash,container,false)
        //init viewmodel
        val application = requireNotNull(this).activity?.application
        val dataSource = context?.let { NoteDatabase.invoke(it).getNoteDao() }
        if (application!=null){
            val homeViewmodelFactory = dataSource?.let { HomeViewmodelFactory(it, application) }
            viewmodel = ViewModelProviders.of(this,homeViewmodelFactory).get(HomeViewmodel::class.java)
            mbinding.viewmodel = viewmodel
            mbinding.lifecycleOwner = this
            getAllNoteDB()
        }
        return mbinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val delete = activity?.findViewById<ImageView>(R.id.toolbar_editicon)
        delete?.visibility = View.VISIBLE
        delete?.setImageResource(R.drawable.ic_delete)

        delete?.setOnClickListener {
            if (deletedNotes!=null){
                if (deletedNotes!!.isEmpty()){
                    context?.let { it1 -> Constants.showToast("Your trash is empty", it1) }
                }else{
                    val builder: AlertDialog.Builder =
                        AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                    builder.setTitle("Delete").setIcon(null)
                        .setMessage("Are you sure to delete all notes \nforever?")
                    builder.setPositiveButton(R.string.no) { dialog, which ->
                        dialog.dismiss()
                    }
                    builder.setNegativeButton(R.string.yes) { dialog, which ->
                        if (deletedNotes!=null){
                            for (i in 0 until deletedNotes!!.size){
                                viewmodel.deleteNoteById(deletedNotes!![i].noteId)
                            }
                        }
                    }
                    builder.show()
                }
            }

        }

    }

    private fun getAllNoteDB() {
        viewmodel.getallNotes()
        viewmodel.allNotes.observe(this, Observer {notes->
            Log.d("@@@@@",notes.toString())
            notesize = notes.size
            deletedNotes = ArrayList()
            for (i in 0 until notesize){
                if (notes[i].isDeleted==1)
                    deletedNotes!!.add(notes[i])
            }
            if (deletedNotes!!.isEmpty()){
                mbinding.errLayout.root.visibility  = View.VISIBLE
                mbinding.errLayout.errmsg.text = "Your Notes in Trash will be deleted after 7 days"
                // mbinding.adView.visibility = View.GONE
                noteDBAdapter =
                    TrashAdapter(deletedNotes!!, this)
                mbinding.trashRecycler.adapter = noteDBAdapter
                noteDBAdapter?.notifyDataSetChanged()
            }else{
                noteDBAdapter =
                    TrashAdapter(deletedNotes!!, this)
                mbinding.trashRecycler.layoutManager = getLayoutManager(4)
                mbinding.trashRecycler.adapter = noteDBAdapter
                noteDBAdapter?.notifyDataSetChanged()

            }



        })
    }
    private fun getLayoutManager(i:Int): RecyclerView.LayoutManager{
        when(i){
            1->{
                val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
                return linearLayoutManager
            }

            2->{
                val gridLayoutManager = GridLayoutManager(context,3)
                return gridLayoutManager
            }


            3->{
                val stagLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                return stagLayoutManager
            }

            else ->{
                val stagLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                return stagLayoutManager
            }


        }
    }

    override fun takeActionForNotes(actionFor: String, noteId: Long, position: Int) {
        when(actionFor){
            "restorenote"->{
                viewmodel.updateDeleteById(noteId,0)
            }
        }
    }
}
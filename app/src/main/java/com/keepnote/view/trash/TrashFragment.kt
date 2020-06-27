package com.keepnote.view.trash

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.keepnote.HomeScreen
import com.keepnote.NoteListAdapter
import com.keepnote.R
import com.keepnote.databinding.FragmentTrashBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.NoteDatabase
import com.keepnote.notesDB.Notes
import com.keepnote.utils.Constants
import com.keepnote.viewmodel.HomeViewmodel
import com.keepnote.viewmodel.HomeViewmodelFactory

/**
 * A simple [Fragment] subclass.
 */
class TrashFragment : Fragment(),NoteListAdapter.NotesListner,Observer<Any> {
    private  var deletedNotes: ArrayList<Notes>?=null
    private var notesize: Int = 0
    private  var noteDBAdapter: TrashAdapter?=null
    private lateinit var mbinding:FragmentTrashBinding
    lateinit var viewmodel: HomeViewmodel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mbinding = DataBindingUtil.inflate(inflater,R.layout.fragment_trash,container,false)
        return mbinding.root
    }

    override fun onChanged(observer: Any?) {
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Notess"
            //init viewmodel
            val application = requireNotNull(this).activity?.application
            val dataSource = context?.let { NoteDatabase.invoke(it).getNoteDao() }
            if (application!=null){
                val homeViewmodelFactory = dataSource?.let { HomeViewmodelFactory(it, application) }
                viewmodel = ViewModelProviders.of(this,homeViewmodelFactory).get(HomeViewmodel::class.java)
                mbinding.viewmodel = viewmodel
                mbinding.lifecycleOwner = this
            }

            getAllNoteDB()
            viewmodel.initview(mbinding.root).observe(this,this)
            viewmodel.passedData.observe(this,this)



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
        try {
            viewmodel.getallNotes()
            viewmodel.allNotes.observe(this, Observer {notes->
                notesize = notes.size
                deletedNotes = ArrayList()
                for (i in 0 until notesize){
                    if (notes[i].isDeleted==1)
                        deletedNotes?.add(notes[i])
                }
                if (deletedNotes!=null){
                    if (deletedNotes!!.isEmpty()){
                        mbinding.errLayout.root.visibility  = View.VISIBLE
                        mbinding.errLayout.errmsg.text = "Your Notes in Trash will be deleted after 7 days"
                     //    mbinding.adView.visibility = View.GONE
                        noteDBAdapter =
                            TrashAdapter(deletedNotes!!, this)
                        mbinding.trashRecycler.adapter = noteDBAdapter
                        if (activity!=null) (activity as HomeScreen).getAllNoteDBCount()
                        noteDBAdapter?.notifyDataSetChanged()
                    }else{
                        noteDBAdapter =
                            TrashAdapter(deletedNotes!!, this)
                        val layout = StoreSharedPrefData.INSTANCE.getPref("viewas",1,context)
                        mbinding.trashRecycler.layoutManager = getLayoutManager(layout as Int)
                        mbinding.trashRecycler.adapter = noteDBAdapter
                        if (activity!=null) (activity as HomeScreen).getAllNoteDBCount()
                        noteDBAdapter?.notifyDataSetChanged()


                    }
                }

            })
        }catch (e:Exception){
        }

    }

    private fun getLayoutManager(i:Int): RecyclerView.LayoutManager{
        when(i){
            1->{
                return LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            }

            3->{
                return GridLayoutManager(context,3)
            }


            2->{
                return StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }

            else ->{
                return StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
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

    fun getDate(value:String){
        when(value){
            "view_list"->{
                mbinding.trashRecycler.layoutManager = getLayoutManager(1)
                if (activity!=null) (activity as HomeScreen).getAllNoteDBCount()
                noteDBAdapter?.notifyDataSetChanged()
            }
            "view_grid"->{
                mbinding.trashRecycler.layoutManager = getLayoutManager(2)
                if (activity!=null) (activity as HomeScreen).getAllNoteDBCount()
                noteDBAdapter?.notifyDataSetChanged()
            }
        }
    }
}

package com.keepnote.view.favourite

import android.os.Bundle
import android.util.Log
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
import com.keepnote.notesDB.Notes
import com.keepnote.viewmodel.HomeViewmodel
import com.keepnote.viewmodel.HomeViewmodelFactory
import com.raks.roomdatabase.NoteDatabase

/**
 * A simple [Fragment] subclass.
 */
class FavouriteFragment : Fragment(),NoteListAdapter.NotesListner {
    private  var favNotes: ArrayList<Notes>?=null
    private var notesize: Int = 0
    private  var noteDBAdapter: FavouriteAdapter?=null
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
        delete?.visibility = View.GONE
    }

    private fun getAllNoteDB() {
        try {
            viewmodel.getallNotes()
            viewmodel.allNotes.observe(this, Observer {notes->
                Log.d("@@@@@",notes.toString())
                notesize = notes.size
                favNotes = ArrayList()
                for (i in 0 until notesize){
                    if (notes[i].isFavourite==1)
                        favNotes?.add(notes[i])
                }
                if (favNotes!=null){
                    if (favNotes!!.isEmpty()){
                        mbinding.errLayout.root.visibility  = View.VISIBLE
                        mbinding.errLayout.errmsg.text = "Your Favourite Notes Will Be Kept Here."
                        // mbinding.adView.visibility = View.GONE
                        noteDBAdapter =
                            FavouriteAdapter(favNotes!!, this)
                        mbinding.trashRecycler.adapter = noteDBAdapter
                        if (activity!=null) (activity as HomeScreen).getAllNoteDBCount()
                        noteDBAdapter?.notifyDataSetChanged()
                    }else{
                        noteDBAdapter =
                            FavouriteAdapter(favNotes!!, this)
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

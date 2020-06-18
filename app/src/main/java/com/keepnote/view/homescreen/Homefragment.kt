package com.keepnote.view.homescreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.keepnote.EditNote
import com.keepnote.HomeScreen
import com.keepnote.NoteListAdapter
import com.keepnote.R
import com.keepnote.databinding.HomefragmentBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.Notes
import com.keepnote.utils.Constants
import com.keepnote.utils.PassDataToFragListner
import com.keepnote.viewmodel.HomeViewmodel
import com.keepnote.viewmodel.HomeViewmodelFactory
import com.raks.roomdatabase.NoteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class Homefragment : Fragment(),NoteListAdapter.NotesListner,Observer<Any>{

    private lateinit var nonDeletedNotes: java.util.ArrayList<Notes>
    private  var noteDBAdapter: NoteListAdapter?=null
    lateinit var mbinding:HomefragmentBinding
    lateinit var viewmodel: HomeViewmodel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mbinding = DataBindingUtil.inflate(inflater,R.layout.homefragment,container,false)
        return  mbinding.root

    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val delete = activity?.findViewById<ImageView>(R.id.toolbar_editicon)
        delete?.visibility = View.GONE
        getAllNoteDB()
        mbinding.swiperefresh.setOnRefreshListener {
            GlobalScope.launch {
                delay(1000L)
                activity?.runOnUiThread {
                    getAllNoteDB()
                    mbinding.swiperefresh.isRefreshing =false
                }

            }

        }

    }

    private fun getAllNoteDB() {
        viewmodel.getallNotes()
        viewmodel.allNotes.observe(this, Observer {notes->
            Log.d("@@@@@observed",notes.toString())
            val notesize = notes.size
            nonDeletedNotes = ArrayList()
            for (i in 0 until notesize){
                    if (notes[i].isDeleted==0 && notes[i].isFavourite==0)
                        nonDeletedNotes.add(notes[i])
                }
            if (nonDeletedNotes.isEmpty()){
                mbinding.swiperefresh.visibility = View.GONE
                // mbinding.adView.visibility = View.GONE
                mbinding.errLayout.root.visibility  = View.VISIBLE
            }else{
                mbinding.swiperefresh.visibility = View.VISIBLE
                mbinding.errLayout.root.visibility  = View.GONE
                noteDBAdapter =
                    NoteListAdapter(nonDeletedNotes, this)
                val layout = StoreSharedPrefData.INSTANCE.getPref("viewas",1,context)
                mbinding.notelistRecycler.layoutManager = getLayoutManager(layout as Int)
                mbinding.notelistRecycler.adapter = noteDBAdapter
                noteDBAdapter?.notifyDataSetChanged()

            }



        })
    }

    private fun getLayoutManager(i:Int):RecyclerView.LayoutManager{
        when(i){
            1->{
              val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
                return linearLayoutManager
            }

            2->{
               val stagLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                return stagLayoutManager
            }
            3->{
                val gridLayoutManager = GridLayoutManager(context,3)
                return gridLayoutManager
            }

            else ->{
               val stagLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                return stagLayoutManager
            }


        }
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
            if (context!=null)
            mbinding.swiperefresh.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.accestcolor))

            viewmodel.initview(mbinding.root).observe(this,this)
            viewmodel.passedData.observe(this,this)

    }

    override fun takeActionForNotes(actionFor: String, noteId: Long, position: Int) {
        when(actionFor){
            "deletenotebyid"->{
                try {
                    viewmodel.updateDeleteById(noteId,1)
                }catch (e:java.lang.Exception){

                }

            }
            "updatelockbyid"->{
                try {
                    if (nonDeletedNotes[position].islocked==1)
                    viewmodel.updateLockbyId(noteId,0)
                    else viewmodel.updateLockbyId(noteId,1)
                }catch (e:java.lang.Exception){

                }

            }
        }
    }

    override fun onChanged(observer: Any?) {
        if (observer is View) {
            when (observer.id) {
                R.id.fab -> {
                    val addNoteIntent = Intent(context, EditNote::class.java)
                    addNoteIntent.putExtra("from", 1)
                    startActivity(addNoteIntent)

                }
            }
        }
    }



    fun getDate(value:String){
        when(value){
            "view_list"->{
                mbinding.notelistRecycler.layoutManager = getLayoutManager(1)
                noteDBAdapter?.notifyDataSetChanged()
            }
            "view_grid"->{
                mbinding.notelistRecycler.layoutManager = getLayoutManager(2)
                noteDBAdapter?.notifyDataSetChanged()
            }
        }
    }
}

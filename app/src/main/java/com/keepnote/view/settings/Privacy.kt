package com.keepnote.view.settings

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.keepnote.NoteListAdapter
import com.keepnote.R
import com.keepnote.databinding.ActivityPrivacyBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.Notes
import com.keepnote.utils.Constants
import com.keepnote.view.trash.TrashAdapter
import com.keepnote.viewmodel.HomeViewmodel
import com.keepnote.viewmodel.HomeViewmodelFactory
import com.raks.roomdatabase.NoteDatabase
import kotlinx.android.synthetic.main.toolbar.view.*

class Privacy : AppCompatActivity(),NoteListAdapter.NotesListner {

    private  var  lokedNotes: ArrayList<Notes>?=null
    private var notesize: Int=0
    private var selectedQuestionPosition: Int=0
    private lateinit var animshake: Animation
    lateinit var binding:ActivityPrivacyBinding
    private var patternFor=""
    private var temppasword=""
    private  var noteDBAdapter: TrashAdapter?=null
    private lateinit var viewmodel: HomeViewmodel
    var securityQuestion = arrayOf("What is your father's name?","What is your mother's name?","" +
            "What is your first girl friend name?","What is your ID card numbers?","What is your first company name?",
    "What is your favorite actor?","What is your first pet name?")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_privacy)
        binding.toolbarPrivacy.toolbarSearch.visibility = View.VISIBLE
        binding.toolbarPrivacy.toolbar.title=""
        binding.toolbarPrivacy.toolbartitle.text = "Privacy"
        setSupportActionBar(binding.toolbarPrivacy.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        animshake = AnimationUtils.loadAnimation(this,R.anim.shake)

        val lockpattern:String? = StoreSharedPrefData.INSTANCE.getPref("lockpattern",0,this).toString()
        if (lockpattern!=null){
            if (lockpattern == "1"){

                //init viewmodel
                val application = requireNotNull(this).application
                val dataSource = NoteDatabase.invoke(this).getNoteDao()
                val homeViewmodelFactory = HomeViewmodelFactory(dataSource,application)
                viewmodel = ViewModelProviders.of(this,homeViewmodelFactory).get(HomeViewmodel::class.java)
                binding.viewmodel = viewmodel
                binding.lifecycleOwner = this
                binding.patternLl.visibility = View.GONE
                binding.privacynotesLl.visibility= View.VISIBLE
                getAllNoteDB()


            }
        }


        binding.patternLockView.addPatternLockListener(object: PatternLockViewListener {
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                if (patternFor.isEmpty()){
                    temppasword = PatternLockUtils.patternToString(binding.patternLockView,pattern)
                    Log.d("@@@@@temp",temppasword)
                    binding.privacyActionText.text = "Confirm your unlock pattern"
                    binding.patternLockView.clearPattern()
                    patternFor="confirmpattern"
                }else if (patternFor=="confirmpattern"){
                    val confirmpattern = PatternLockUtils.patternToString(binding.patternLockView,pattern)
                    if (temppasword==confirmpattern){
                        StoreSharedPrefData.INSTANCE.savePrefValue("securitypattern",confirmpattern,this@Privacy)
                        binding.toolbarPrivacy.toolbar.title="Set security question"
                        binding.secquestionLl.visibility = View.VISIBLE
                        binding.patternLl.visibility = View.GONE
                    }else{
                        Constants.showToast("password not match",this@Privacy)
                        binding.privacyActionText.text ="Patterns do not match, try again!"
                        binding.privacyActionText.setTextColor(ContextCompat.getColor(this@Privacy,R.color.failurecolor))
                        binding.privacyActionText.startAnimation(animshake)
                        binding.patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)

                    }
                }

            }

            override fun onCleared() {

            }

            override fun onStarted() {

            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {

            }
        })

        val questionAdapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,securityQuestion)
        questionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.securitySpinner.adapter = questionAdapter
        binding.securitySpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedQuestionPosition = position

            }
        }

        binding.submitBtn.setOnClickListener {
            if (selectedQuestionPosition.toString().isNotEmpty()){
               val selectedAnswer = binding.securityAnswer.text.toString()
                if (selectedAnswer.isNotEmpty()){
                    binding.toolbarPrivacy.toolbar.title="Privacy"
                    StoreSharedPrefData.INSTANCE.savePrefValue("securityquestion",selectedQuestionPosition,this)
                    StoreSharedPrefData.INSTANCE.savePrefValue("securityanswer",selectedAnswer,this)
                    StoreSharedPrefData.INSTANCE.savePrefValue("lockpattern",1,this)
                    binding.privacynotesLl.visibility= View.VISIBLE
                    binding.secquestionLl.visibility = View.GONE
                    Constants.showToast("Set successfully!",this)

                }else{
                    Constants.showToast("Input cannot be empty",this)
                }
            }else{
                Constants.showToast("Please select question list",this)
            }
        }

    }

    private fun getAllNoteDB() {
        viewmodel.getallNotes()
        viewmodel.allNotes.observe(this, Observer {notes->
            Log.d("@@@@@",notes.toString())
            notesize = notes.size
            lokedNotes = ArrayList()
            for (i in 0 until notesize){
                if (notes[i].islocked==1)
                    lokedNotes!!.add(notes[i])
            }
            if (lokedNotes!!.isEmpty()){
                binding.errLayout.root.visibility  = View.VISIBLE
                binding.errLayout.errmsg.text = "Your Notes privacy will be kept here"
                // mbinding.adView.visibility = View.GONE
                noteDBAdapter =
                    TrashAdapter(lokedNotes!!, this)
                binding.trashRecycler.adapter = noteDBAdapter
                noteDBAdapter?.notifyDataSetChanged()
            }else{
                noteDBAdapter =
                    TrashAdapter(lokedNotes!!, this)
                binding.trashRecycler.layoutManager = getLayoutManager(4)
                binding.trashRecycler.adapter = noteDBAdapter
                noteDBAdapter?.notifyDataSetChanged()

            }



        })
    }

    private fun getLayoutManager(i:Int): RecyclerView.LayoutManager{
        when(i){
            1->{
                val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
                return linearLayoutManager
            }

            2->{
                val gridLayoutManager = GridLayoutManager(this,3)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home-> {
                onBackPressed()

            }
        }
        return true
    }

    override fun takeActionForNotes(actionFor: String, noteId: Long, position: Int) {

    }
}
